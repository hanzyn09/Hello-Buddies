package com.buddy.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.buddy.dto.BuddyDto;
import com.buddy.dto.BuddyFileDto;

public interface BuddyService {

	List<BuddyDto> selectBuddyList();

	String createBuddy(String name, MultipartHttpServletRequest request);

	BuddyDto selectBuddyDetail(int buddyId);
	
	String updateState(int buddyId, String action);
	
	String deleteBuddy(int buddyId);

	BuddyFileDto selectBuddyFileInfo(int imageId, int buddyId);

	String updateDate(String action);
}
