package tamagotchi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import tamagotchi.common.FileUtils;
import tamagotchi.dto.TamagotchiDto;
import tamagotchi.dto.TamagotchiFileDto;
import tamagotchi.mapper.TamagotchiMapper;

@Slf4j
@Service
public class TamagotchiServiceImpl implements TamagotchiService {
    @Autowired
    private TamagotchiMapper tamagochiMapper;

    @Autowired
    private FileUtils fileUtils;

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    @Override
    public List<TamagotchiDto> selectTamagotchiList() {
        return tamagochiMapper.selectTamagotchiList();
    }

    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void autoUpdateState() {
        log.info("자동 타마고치 상태 업데이트 시작");
        List<TamagotchiDto> tamagotchiList = selectTamagotchiList();
        if (!CollectionUtils.isEmpty(tamagotchiList)) {
            updateDate("day");
        }
        log.info("자동 타마고치 상태 업데이트 완료");
    }

    @Override
    public String createTamagotchi(String name, MultipartHttpServletRequest request) {
    	String message = "";
    	
        TamagotchiDto tamagotchiDto = new TamagotchiDto();
        tamagotchiDto.setName(name);
        tamagotchiDto.setLevelNumber(1);
        tamagotchiDto.setHunger(50);
        tamagotchiDto.setFatigue(50);
        tamagotchiDto.setHappiness(50);
        tamagochiMapper.insertTamagotchi(tamagotchiDto);

        try {
            List<TamagotchiFileDto> fileInfoList = fileUtils.parseFileInfo(tamagotchiDto.getTamagotchiId(), request);
            if (!CollectionUtils.isEmpty(fileInfoList)) {
                tamagochiMapper.insertTamagotchiFileList(fileInfoList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
        message = tamagotchiDto.getName() + "(을)를 데려왔습니다!";
        return message;
    }

    @Override
    public TamagotchiDto selectTamagotchiDetail(int tamagotchiId) {
        TamagotchiDto tamagotchiDto = tamagochiMapper.selectTamagotchiDetail(tamagotchiId);
        List<TamagotchiFileDto> fileInfoList = tamagochiMapper.selectTamagotchiFileList(tamagotchiId);
        tamagotchiDto.setFileInfoList(fileInfoList);
        return tamagotchiDto;
    }

    @Override
    public String updateState(int tamagotchiId, String action) {
        TamagotchiDto tamagotchiDto = tamagochiMapper.selectTamagotchiDetail(tamagotchiId);
        TamagotchiDto tamagotchiDtoTmp = new TamagotchiDto();
        tamagotchiDtoTmp.setTamagotchiId(tamagotchiId);

        String message = "";

        // 상태 업데이트 메서드 호출
        message = updateTamagotchiState(action, tamagotchiDto, tamagotchiDtoTmp);

        tamagochiMapper.updateState(tamagotchiDtoTmp);
        return message;
    }

    private String updateTamagotchiState(String action, TamagotchiDto tamagotchiDto, TamagotchiDto tamagotchiDtoTmp) {
        int oldLevelNumber = tamagotchiDto.getLevelNumber();
        int oldHunger = tamagotchiDto.getHunger();
        int oldFatigue = tamagotchiDto.getFatigue();
        int oldHappiness = tamagotchiDto.getHappiness();
        String message = "";

        switch (action) {
            case "hunger":
                message += tamagotchiDto.getName() + "(은)는 먹이를 먹었습니다.";
                message = updateHunger(tamagotchiDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            case "sleep":
                message += tamagotchiDto.getName() + "(은)는 푹 잤습니다.";
                message = updateSleep(tamagotchiDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            case "play":
                message += tamagotchiDto.getName() + "(은)는 신나게 놀았습니다.";
                message = updatePlay(tamagotchiDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            case "day":
                //message += "하루가 경과했습니다.";
                message = updateDay(tamagotchiDtoTmp, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, message);
                break;
            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }

        return message;
    }

    private String updateHunger(TamagotchiDto tamagotchiDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newHunger = Math.min(Math.max(oldHunger - 5, MIN_VALUE), MAX_VALUE);
        tamagotchiDtoTmp.setHunger(newHunger);
        tamagotchiDtoTmp.setFatigue(oldFatigue);
        tamagotchiDtoTmp.setHappiness(oldHappiness);
        tamagotchiDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (newHunger == 0 && oldFatigue == 0 && oldHappiness == 100) {
            tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
            tamagotchiDtoTmp.setFatigue(50);
            tamagotchiDtoTmp.setHunger(50);
            tamagotchiDtoTmp.setHappiness(50);
            message += "그리고 진화했습니다!";
        }
        return message;
    }

    private String updateSleep(TamagotchiDto tamagotchiDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newFatigue = Math.min(Math.max(oldFatigue - 5, MIN_VALUE), MAX_VALUE);
        tamagotchiDtoTmp.setFatigue(newFatigue);
        tamagotchiDtoTmp.setHunger(oldHunger);
        tamagotchiDtoTmp.setHappiness(oldHappiness);
        tamagotchiDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (oldHunger == 0 && newFatigue == 0 && oldHappiness == 100) {
            tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
            tamagotchiDtoTmp.setFatigue(50);
            tamagotchiDtoTmp.setHunger(50);
            tamagotchiDtoTmp.setHappiness(50);
            message += " 그리고 진화했습니다!";
        }
        return message;
    }

    private String updatePlay(TamagotchiDto tamagotchiDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newFatigue = Math.min(Math.max(oldFatigue + 5, MIN_VALUE), MAX_VALUE);
        int newHappiness = Math.min(Math.max(oldHappiness + 5, MIN_VALUE), MAX_VALUE);
        tamagotchiDtoTmp.setFatigue(newFatigue);
        tamagotchiDtoTmp.setHappiness(newHappiness);
        tamagotchiDtoTmp.setHunger(oldHunger);
        tamagotchiDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (oldHunger == 0 && newFatigue == 0 && newHappiness == 100) {
            tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
            tamagotchiDtoTmp.setFatigue(50);
            tamagotchiDtoTmp.setHunger(50);
            tamagotchiDtoTmp.setHappiness(50);
            message += " 그리고 진화했습니다!";
        }
        return message;
    }

    private String updateDay(TamagotchiDto tamagotchiDtoTmp, int oldLevelNumber, int oldHunger, int oldFatigue, int oldHappiness, String message) {
        int newHunger = Math.min(Math.max(oldHunger + 10, MIN_VALUE), MAX_VALUE);
        int newFatigue = Math.min(Math.max(oldFatigue + 10, MIN_VALUE), MAX_VALUE);
        int newHappiness = Math.min(Math.max(oldHappiness - 10, MIN_VALUE), MAX_VALUE);

        tamagotchiDtoTmp.setHunger(newHunger);
        tamagotchiDtoTmp.setFatigue(newFatigue);
        tamagotchiDtoTmp.setHappiness(newHappiness);
        tamagotchiDtoTmp.setLevelNumber(oldLevelNumber); // 레벨 유지

        // 레벨업 조건
        if (newHunger == 0 && newFatigue == 0 && newHappiness == 100) {
            tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
            tamagotchiDtoTmp.setFatigue(50);
            tamagotchiDtoTmp.setHunger(50);
            tamagotchiDtoTmp.setHappiness(50);
            //message += " 그리고 진화했습니다!";
        }
        return message;
    }

    @Override
    public String deleteTamagotchi(int tamagotchiId) {
        tamagochiMapper.deleteTamagotchi(tamagotchiId);
        return "입양이 완료되었습니다.";
    }

    @Override
    public TamagotchiFileDto selectTamagotchiFileInfo(int imageId, int tamagotchiId) {
        return tamagochiMapper.selectTamagotchiFileInfo(imageId, tamagotchiId);
    }

    @Override
    public String updateDate(String action) {
        List<TamagotchiDto> list = this.selectTamagotchiList();
        String message = "하루가 경과했습니다.";
        for (TamagotchiDto tamagotchi : list) {
            TamagotchiDto tamagotchiDtoTmp = new TamagotchiDto();
            tamagotchiDtoTmp.setTamagotchiId(tamagotchi.getTamagotchiId());
            message = updateTamagotchiState(action, tamagotchi, tamagotchiDtoTmp);
            tamagochiMapper.updateState(tamagotchiDtoTmp);
        }
        return message;
    }
}
