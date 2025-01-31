package com.buddy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.buddy.common.FileUtils;
import com.buddy.dto.BuddyDto;
import com.buddy.dto.BuddyFileDto;
import com.buddy.mapper.BuddyMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BuddyServiceImpl implements BuddyService {
    @Autowired
    private BuddyMapper buddyMapper;

    @Autowired
    private FileUtils fileUtils;

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    @Override
    public List<BuddyDto> selectBuddyList() {
        return buddyMapper.selectBuddyList();
    }

    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void autoUpdateState() {
        log.info("자동 타마고치 상태 업데이트 시작");
        List<BuddyDto> buddyList = selectBuddyList();
        if (!CollectionUtils.isEmpty(buddyList)) {
            updateDate("day");
        }
        log.info("자동 타마고치 상태 업데이트 완료");
    }

    @Override
    public String createBuddy(String name, MultipartHttpServletRequest request) {
    	String message = "";
    	
        BuddyDto buddyDto = new BuddyDto();
        buddyDto.setName(name);
        buddyDto.setLevelNumber(1);
        buddyDto.setHunger(50);
        buddyDto.setFatigue(50);
        buddyDto.setHappiness(50);
        buddyMapper.insertBuddy(buddyDto);

        try {
            List<BuddyFileDto> fileInfoList = fileUtils.parseFileInfo(buddyDto.getBuddyId(), request);
            if (!CollectionUtils.isEmpty(fileInfoList)) {
                buddyMapper.insertBuddyFileList(fileInfoList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
        message = buddyDto.getName() + "(을)를 데려왔습니다!";
        return message;
    }

    @Override
    public BuddyDto selectBuddyDetail(int buddyId) {
        BuddyDto buddyDto = buddyMapper.selectBuddyDetail(buddyId);
        List<BuddyFileDto> fileInfoList = buddyMapper.selectBuddyFileList(buddyId);
        buddyDto.setFileInfoList(fileInfoList);
        return buddyDto;
    }

    @Override
    public String updateState(int buddyId, String action) {
        BuddyDto buddyDto = buddyMapper.selectBuddyDetail(buddyId);
        BuddyDto buddyDtoTmp = new BuddyDto();
        buddyDtoTmp.setBuddyId(buddyId);

        String message = "";

        // 상태 업데이트 메서드 호출
        message = updateBuddyState(action, buddyDto, buddyDtoTmp);

        buddyMapper.updateState(buddyDtoTmp);
        return message;
    }

    private String updateBuddyState(String action, BuddyDto buddyDto, BuddyDto buddyDtoTmp) {
        int oldLevelNumber = buddyDto.getLevelNumber();
        int oldHunger = buddyDto.getHunger();
        int oldFatigue = buddyDto.getFatigue();
        int oldHappiness = buddyDto.getHappiness();
        String message = "";

        switch (action) {
            case "hunger":
                message += buddyDto.getName() + "(은)는 먹이를 먹었습니다.";
                message = updateHunger(buddyDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            case "sleep":
                message += buddyDto.getName() + "(은)는 푹 잤습니다.";
                message = updateSleep(buddyDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            case "play":
                message += buddyDto.getName() + "(은)는 신나게 놀았습니다.";
                message = updatePlay(buddyDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            case "day":
                //message += "하루가 경과했습니다.";
                message = updateDay(buddyDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }

        return message;
    }

    private String updateHunger(BuddyDto buddyDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newHunger = Math.min(Math.max(oldHunger - 5, MIN_VALUE), MAX_VALUE);
        buddyDtoTmp.setHunger(newHunger);
        buddyDtoTmp.setFatigue(oldFatigue);
        buddyDtoTmp.setHappiness(oldHappiness);
        buddyDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (newHunger == 0 && oldFatigue == 0 && oldHappiness == 100) {
            buddyDtoTmp.setLevelNumber(oldLevelNumber + 1);
            buddyDtoTmp.setFatigue(50);
            buddyDtoTmp.setHunger(50);
            buddyDtoTmp.setHappiness(50);
            message += "그리고 진화했습니다!";
        }
        return message;
    }

    private String updateSleep(BuddyDto buddyDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newFatigue = Math.min(Math.max(oldFatigue - 5, MIN_VALUE), MAX_VALUE);
        buddyDtoTmp.setFatigue(newFatigue);
        buddyDtoTmp.setHunger(oldHunger);
        buddyDtoTmp.setHappiness(oldHappiness);
        buddyDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (oldHunger == 0 && newFatigue == 0 && oldHappiness == 100) {
            buddyDtoTmp.setLevelNumber(oldLevelNumber + 1);
            buddyDtoTmp.setFatigue(50);
            buddyDtoTmp.setHunger(50);
            buddyDtoTmp.setHappiness(50);
            message += " 그리고 진화했습니다!";
        }
        return message;
    }

    private String updatePlay(BuddyDto buddyDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newFatigue = Math.min(Math.max(oldFatigue + 5, MIN_VALUE), MAX_VALUE);
        int newHappiness = Math.min(Math.max(oldHappiness + 5, MIN_VALUE), MAX_VALUE);
        buddyDtoTmp.setFatigue(newFatigue);
        buddyDtoTmp.setHappiness(newHappiness);
        buddyDtoTmp.setHunger(oldHunger);
        buddyDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (oldHunger == 0 && newFatigue == 0 && newHappiness == 100) {
            buddyDtoTmp.setLevelNumber(oldLevelNumber + 1);
            buddyDtoTmp.setFatigue(50);
            buddyDtoTmp.setHunger(50);
            buddyDtoTmp.setHappiness(50);
            message += " 그리고 진화했습니다!";
        }
        return message;
    }

    private String updateDay(BuddyDto buddyDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newHunger = Math.min(Math.max(oldHunger + 10, MIN_VALUE), MAX_VALUE);
        int newFatigue = Math.min(Math.max(oldFatigue + 10, MIN_VALUE), MAX_VALUE);
        int newHappiness = Math.min(Math.max(oldHappiness - 10, MIN_VALUE), MAX_VALUE);

        buddyDtoTmp.setHunger(newHunger);
        buddyDtoTmp.setFatigue(newFatigue);
        buddyDtoTmp.setHappiness(newHappiness);
        buddyDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (newHunger == 0 && newFatigue == 0 && newHappiness == 100) {
            buddyDtoTmp.setLevelNumber(oldLevelNumber + 1);
            buddyDtoTmp.setFatigue(50);
            buddyDtoTmp.setHunger(50);
            buddyDtoTmp.setHappiness(50);
            //message += " 그리고 진화했습니다!";
        }
        return message;
    }

    @Override
    public String deleteBuddy(int buddyId) {
        buddyMapper.deleteBuddy(buddyId);
        return "입양이 완료되었습니다.";
    }

    @Override
    public BuddyFileDto selectBuddyFileInfo(int imageId, int buddyId) {
        return buddyMapper.selectBuddyFileInfo(imageId, buddyId);
    }

    @Override
    public String updateDate(String action) {
        List<BuddyDto> list = this.selectBuddyList();
        String message = "하루가 경과했습니다.";
        for (BuddyDto buddy : list) {
            BuddyDto buddyDtoTmp = new BuddyDto();
            buddyDtoTmp.setBuddyId(buddy.getBuddyId());
            message = updateBuddyState(action, buddy, buddyDtoTmp);
            buddyMapper.updateState(buddyDtoTmp);
        }
        return message;
    }
}
