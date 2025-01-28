package tamagotchi.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import tamagotchi.dto.TamagotchiDto;
import tamagotchi.dto.TamagotchiFileDto;
import tamagotchi.service.TamagotchiService;

@Slf4j
@Controller
@RequestMapping("/tamagotchi")
public class TamagotchiController {

    @Autowired
    private TamagotchiService tamagotchiService;
    
    // 게임 설명 화면
    @GetMapping("about.do")
    public String about() {
        return "/tamagotchi/about";  // 다마고치 게임 설명 페이지
    }
    
    // 다마고치 목록 보기
    @GetMapping("/openTamagotchiList.do")
    public ModelAndView openTamagotchiList() throws Exception {
        ModelAndView mv = new ModelAndView("/tamagotchi/tamagotchiList");
        List<TamagotchiDto> list = tamagotchiService.selectTamagotchiList();
        mv.addObject("tamagotchis", list);
        return mv;
    }

    //다마고치 상세페이지 상태 자동 업데이트
    @GetMapping("/getTamagotchiStatus.do")
    @ResponseBody  // JSON 형태로 응답을 반환
    public TamagotchiDto getTamagotchiStatus(@RequestParam("tamagotchiId") int tamagotchiId) {
        TamagotchiDto tamagotchiDto = tamagotchiService.selectTamagotchiDetail(tamagotchiId);
        
        // 서버에서 받은 타마고치 상태를 JSON으로 반환
        return tamagotchiDto;
    }
    
    // 새로운 다마고치 등록 화면
    @GetMapping("/createTamagotchi.do")
    public String createTamagotchiForm() {
        return "/tamagotchi/createTamagotchi";  // 새로운 다마고치 등록 폼 페이지
    }

    // 다마고치 등록 처리
    @PostMapping("/createTamagotchi.do")
    public String createTamagotchi(@RequestParam("name") String name, MultipartHttpServletRequest request) throws Exception {
        tamagotchiService.createTamagotchi(name, request);
        return "redirect:/tamagotchi/openTamagotchiList.do";  // 다마고치 목록 페이지로 리다이렉트
    }

    // 상세 조회 요청을 처리하는 메서드
    @GetMapping("/openTamagotchiDetail.do")
    public ModelAndView openTamagotchiDetail(@RequestParam("tamagotchiId") int tamagotchiId) throws Exception {   	
    	TamagotchiDto tamagotchiDto = tamagotchiService.selectTamagotchiDetail(tamagotchiId);
    
        ModelAndView mv = new ModelAndView("/tamagotchi/tamagotchiDetail");
        mv.addObject("tamagotchi", tamagotchiDto);
        return mv;
    }
    
    //다마고치의 상태 변경을 처리하는 메서드
    @PostMapping("/updateState.do")
    public String updateState(@RequestParam("tamagotchiId") int tamagotchiId, @RequestParam("state") String state, RedirectAttributes redirectAttributes) {
        try {
            // tamagotchiId가 유효한지 체크하는 로직 추가 가능 (예: 존재하는 타마고치인지 확인)
            if (tamagotchiId <= 0) {
                throw new IllegalArgumentException("Invalid tamagotchiId: " + tamagotchiId);
            }

            String message = "";
            
            switch (state) {
                case "hunger":
                	message = tamagotchiService.updateState(tamagotchiId, "hunger");
                    break;
                case "sleep":
                	message = tamagotchiService.updateState(tamagotchiId, "sleep");
                    break;
                case "play":
                	message = tamagotchiService.updateState(tamagotchiId, "play");
                    break;
                case "delete":
                	message = tamagotchiService.deleteTamagotchi(tamagotchiId);
                    return "redirect:/tamagotchi/openTamagotchiList.do";  // 다마고치 목록 페이지로 리다이렉트
                default:
                    throw new IllegalArgumentException("Invalid state: " + state);  // 잘못된 상태일 경우 예외 던짐
            }
            
            // 리다이렉트할 때 알림 메시지 전달
            redirectAttributes.addFlashAttribute("alertMessage", message);
            
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 상세 페이지로 리다이렉트 (GET 방식)
        return "redirect:/tamagotchi/openTamagotchiDetail.do?tamagotchiId=" + tamagotchiId;
    }

    // 파일 다운로드 요청을 처리하는 메서드 
    @GetMapping("/downloadTamagotchiFile.do")
    public void downloadTamagotchiFile(@RequestParam("imageId") int imageId, @RequestParam("tamagotchiId") int tamagotchiId, HttpServletResponse response) throws Exception {
        // idx와 boardIdx가 일치하는 파일 정보를 조회
    	TamagotchiFileDto tamagotchiFileDto = tamagotchiService.selectTamagotchiFileInfo(imageId, tamagotchiId);
        if (ObjectUtils.isEmpty(tamagotchiFileDto)) {
            return;
        }
        
        // 원본 파일 저장 위치에서 파일을 읽어서 호출(요청)한 곳으로 파일을 응답으로 전달
        Path path = Paths.get(tamagotchiFileDto.getStoredFilePath());
        byte[] file = Files.readAllBytes(path);
        
        response.setContentType("application/octet-stream");
        response.setContentLength(file.length);
        response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(tamagotchiFileDto.getOriginalFileName(), "UTF-8") + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.getOutputStream().write(file);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    //시간 경과를 처리하는 메서드
    @PostMapping("/updateDate.do")
    public String updateDate(@RequestParam("state") String state, RedirectAttributes redirectAttributes) {
    	String message = "";
    	
        try {       
        	switch (state) {
                case "day":
                	message = tamagotchiService.updateDate("day");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid state: " + state);  // 잘못된 상태일 경우 예외 던짐
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 리다이렉트할 때 알림 메시지 전달
        redirectAttributes.addFlashAttribute("alertMessage", message);
        
        return "redirect:/tamagotchi/openTamagotchiList.do";  // 다마고치 목록 페이지로 리다이렉트
    }
}
