package com.buddy.controller;

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

import com.buddy.dto.BuddyDto;
import com.buddy.dto.BuddyFileDto;
import com.buddy.service.BuddyService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("buddy")
public class BuddyController {

	@Autowired
	private BuddyService BuddyService;

	// 게임 설명 화면
	@GetMapping("about.do")
	public String about() {
		return "buddy/about"; // 버디 게임 설명 페이지
	}

	// 버디 목록 보기
	@GetMapping("openBuddyList.do")
	public ModelAndView openBuddyList() throws Exception {
		ModelAndView mv = new ModelAndView("buddy/buddyList");
		List<BuddyDto> list = BuddyService.selectBuddyList();
		mv.addObject("buddies", list);
		return mv;
	}

	// 버디 목록 상태 자동 업데이트
	@GetMapping("fetchBuddy.do")
	@ResponseBody // JSON 형태로 응답을 반환
	public List<BuddyDto> fetchBuddy() {
		List<BuddyDto> list = BuddyService.selectBuddyList();

		// 서버에서 받은 버디 상태를 JSON으로 반환
		return list;
	}

	// 새로운 버디 등록 화면
	@GetMapping("createBuddy.do")
	public String createBuddyForm() {
		return "buddy/createBuddy"; // 새로운 버디 등록 폼 페이지
	}

	// 버디 등록 처리
	@PostMapping("createBuddy.do")
	public String createBuddy(@RequestParam("name") String name, MultipartHttpServletRequest request,
			RedirectAttributes redirectAttributes) throws Exception {
		String message = BuddyService.createBuddy(name, request);
		redirectAttributes.addFlashAttribute("alertMessage", message);
		return "redirect:openBuddyList.do"; // 버디 목록 페이지로 리다이렉트
	}

	// 상세 조회 요청을 처리하는 메서드
	@GetMapping("openBuddyDetail.do")
	public ModelAndView openBuddyDetail(@RequestParam("buddyId") int buddyId) throws Exception {
		BuddyDto buddyDto = BuddyService.selectBuddyDetail(buddyId);

		ModelAndView mv = new ModelAndView("buddy/buddyDetail");
		mv.addObject("buddy", buddyDto);
		return mv;
	}

	// 버디 상세페이지 상태 자동 업데이트
	@GetMapping("fetchBuddyDetail.do")
	@ResponseBody // JSON 형태로 응답을 반환
	public BuddyDto fetchBuddyDetail(@RequestParam("buddyId") int buddyId) {
		BuddyDto buddyDto = BuddyService.selectBuddyDetail(buddyId);

		// 서버에서 받은 버디 상태를 JSON으로 반환
		return buddyDto;
	}

	// 버디의 상태 변경을 처리하는 메서드
	@PostMapping("updateState.do")
	public String updateState(@RequestParam("buddyId") int buddyId, @RequestParam("state") String state,
			RedirectAttributes redirectAttributes) {
		try {
			// buddyId가 유효한지 체크하는 로직 추가 가능 (예: 존재하는 버디인지 확인)
			if (buddyId <= 0) {
				throw new IllegalArgumentException("Invalid buddyId: " + buddyId);
			}

			String message = "";

			switch (state) {
			case "hunger":
				message = BuddyService.updateState(buddyId, "hunger");
				break;
			case "sleep":
				message = BuddyService.updateState(buddyId, "sleep");
				break;
			case "play":
				message = BuddyService.updateState(buddyId, "play");
				break;
			case "delete":
				message = BuddyService.deleteBuddy(buddyId);
				redirectAttributes.addFlashAttribute("alertMessage", message);
				return "redirect:openBuddyList.do"; // 버디 목록 페이지로 리다이렉트
			default:
				throw new IllegalArgumentException("Invalid state: " + state); // 잘못된 상태일 경우 예외 던짐
			}

			redirectAttributes.addFlashAttribute("alertMessage", message);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 상세 페이지로 리다이렉트 (GET 방식)
		return "redirect:openBuddyDetail.do?buddyId=" + buddyId;
	}

	// 파일 다운로드 요청을 처리하는 메서드
	@GetMapping("downloadBuddyFile.do")
	public void downloadBuddyFile(@RequestParam("imageId") int imageId,
			@RequestParam("buddyId") int buddyId, HttpServletResponse response) throws Exception {
		// idx와 boardIdx가 일치하는 파일 정보를 조회
		BuddyFileDto buddyFileDto = BuddyService.selectBuddyFileInfo(imageId, buddyId);
		if (ObjectUtils.isEmpty(buddyFileDto)) {
			return;
		}

		// 원본 파일 저장 위치에서 파일을 읽어서 호출(요청)한 곳으로 파일을 응답으로 전달
		Path path = Paths.get(buddyFileDto.getStoredFilePath());
		byte[] file = Files.readAllBytes(path);

		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setHeader("Content-Disposition", "attachment; fileName=\""
				+ URLEncoder.encode(buddyFileDto.getOriginalFileName(), "UTF-8") + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.getOutputStream().write(file);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	// 시간 경과를 처리하는 메서드
	@PostMapping("updateDate.do")
	public String updateDate(@RequestParam("state") String state, RedirectAttributes redirectAttributes) {
		String message = "";

		try {
			switch (state) {
			case "day":
				message = BuddyService.updateDate("day");
				break;
			default:
				throw new IllegalArgumentException("Invalid state: " + state); // 잘못된 상태일 경우 예외 던짐
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 리다이렉트할 때 알림 메시지 전달
		redirectAttributes.addFlashAttribute("alertMessage", message);

		return "redirect:openBuddyList.do"; // 버디 목록 페이지로 리다이렉트
	}
}
