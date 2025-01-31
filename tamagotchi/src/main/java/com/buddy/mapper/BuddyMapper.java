package com.buddy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.buddy.dto.BuddyDto;
import com.buddy.dto.BuddyFileDto;

@Mapper
public interface BuddyMapper {
	List<BuddyDto> selectBuddyList();

	void insertBuddy(BuddyDto buddyDto);

	void insertBuddyFileList(List<BuddyFileDto> fileInfoList);

	BuddyDto selectBuddyDetail(int buddyId);
	
	void updateState(BuddyDto buddyDtoTmp);
	
	void deleteBuddy(int buddyId);

	List<BuddyFileDto> selectBuddyFileList(int buddyId);
	
	BuddyFileDto selectBuddyFileInfo(@Param("imageId") int imageId, @Param("buddyId") int buddyId);
	
}
