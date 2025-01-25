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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

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
    
    // 새로운 다마고치 등록 화면
    @GetMapping("/tamagotchiIntroduction.do")
    public String tamagotchiIntroduction() {
        return "/tamagotchi/tamagotchiIntroduction";  // 새로운 다마고치 등록 폼 페이지
    }
    // 게임 설명 화면
    @GetMapping("tamagotchiDescription.do")
    public String tamagotchiDescription() {
        return "/tamagotchi/tamagotchiDescription";  // 다마고치 게임 설명 페이지
    }
    
    // 다마고치 목록 보기
    @GetMapping("/openTamagotchiList.do")
    public ModelAndView openTamagotchiList() throws Exception {
        ModelAndView mv = new ModelAndView("/tamagotchi/tamagotchiList");
        List<TamagotchiDto> list = tamagotchiService.selectTamagotchiList();
        mv.addObject("tamagotchis", list);
        return mv;
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

    // 다마고치 상태 수정
    @PostMapping("/updatePlay.do")
    public String updatePlay(@RequestParam("tamagotchiId") int tamagotchiId) {
        tamagotchiService.updatePlay(tamagotchiId);
        return "redirect:/tamagotchi/openTamagotchiDetail.do?tamagotchiId=" + tamagotchiId;  // 다마고치 상세 페이지로 리다이렉트
    }

    @PostMapping("/updateHunger.do")
    public String updateHunger(@RequestParam("tamagotchiId") int tamagotchiId) {
        tamagotchiService.updateHunger(tamagotchiId);
        return "redirect:/tamagotchi/openTamagotchiDetail.do?tamagotchiId=" + tamagotchiId;  // 다마고치 상세 페이지로 리다이렉트
    }
    
    @PostMapping("/updateSleep.do")
    public String updateSleep(@RequestParam("tamagotchiId") int tamagotchiId) {
        tamagotchiService.updateSleep(tamagotchiId);
        return "redirect:/tamagotchi/openTamagotchiDetail.do?tamagotchiId=" + tamagotchiId;  // 다마고치 상세 페이지로 리다이렉트
    }
    
    @PostMapping("/updateDay.do")
    public String updateDay() {
        tamagotchiService.updateDay();
        return "redirect:/tamagotchi/openTamagotchiList.do";  // 다마고치 목록 페이지로 리다이렉트
    }
    
    // 삭제 요청을 처리할 메서드
    @PostMapping("/deleteTamagotchi.do")
    public String deleteTamagotchi(@RequestParam("tamagotchiId") int tamagotchiId) throws Exception {
        tamagotchiService.deleteTamagotchi(tamagotchiId);
        return "redirect:/tamagotchi/openTamagotchiList.do";  // 다마고치 목록 페이지로 리다이렉트
    }
    
    // 파일 다운로드 요청을 처리하는 메서드 
    @GetMapping("/downloadTamagotchiFile.do")
    public void downloadTamagotchiFile(@RequestParam("imageId") int imageId, @RequestParam("tamagotchiId") int tamagotchiId, HttpServletResponse response) throws Exception {
        // idx와 boardIdx가 일치하는 파일 정보를 조회
    	log.info(">>>>>>>>");
    	TamagotchiFileDto tamagotchiFileDto = tamagotchiService.selectTamagotchiFileInfo(imageId, tamagotchiId);
    	log.info(">>>>>>>>>>>>>>>>");
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
}
