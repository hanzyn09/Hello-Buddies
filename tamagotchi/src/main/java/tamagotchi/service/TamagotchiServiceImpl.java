package tamagotchi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class TamagotchiServiceImpl implements TamagotchiService{
	@Autowired
    private TamagotchiMapper tamagochiMapper;
	
	@Autowired
    private FileUtils fileUtils;
	
	@Override
	public List<TamagotchiDto> selectTamagotchiList() {
		return tamagochiMapper.selectTamagotchiList();
	}
	
	@Override
	public void createTamagotchi(String name, MultipartHttpServletRequest request) {
		 // 1. 다마고치 등록
		TamagotchiDto tamagotchiDto = new TamagotchiDto();
        tamagotchiDto.setName(name);
        tamagotchiDto.setLevelNumber(1);
		tamagotchiDto.setHunger(50);
		tamagotchiDto.setFatigue(50);
		tamagotchiDto.setHappiness(50);
		tamagochiMapper.insertTamagotchi(tamagotchiDto);
		
		// 2. 이미지 업로드 처리 
		try {
            // 첨부 파일을 디스크에 저장하고, 첨부 파일 정보를 반환
            List<TamagotchiFileDto> fileInfoList = fileUtils.parseFileInfo(tamagotchiDto.getTamagotchiId() , request);
            // 첨부 파일 정보를 DB에 저장 (이미지가 있을 경우)
            if (!CollectionUtils.isEmpty(fileInfoList)) {
            	tamagochiMapper.insertTamagotchiFileList(fileInfoList);
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        } 
	}

	@Override
	public TamagotchiDto selectTamagotchiDetail(int tamagotchiId) {
		TamagotchiDto tamagotchiDto = tamagochiMapper.selectTamagotchiDetail(tamagotchiId);
		List<TamagotchiFileDto> tamagotchiFileInfoList = tamagochiMapper.selectTamagotchiFileList(tamagotchiId);
		tamagotchiDto.setFileInfoList(tamagotchiFileInfoList);
        
	    return tamagotchiDto;
	}

	@Override
	public void updateState(int tamagotchiId, String action) {
	    TamagotchiDto tamagotchiDto = tamagochiMapper.selectTamagotchiDetail(tamagotchiId);
	    TamagotchiDto tamagotchiDtoTmp = new TamagotchiDto();
	    
	    tamagotchiDtoTmp.setTamagotchiId(tamagotchiId);

	    // 최소값 0, 최대값 100 설정
	    int minValue = 0;
	    int maxValue = 100;

	    switch (action) {
	        case "hunger":
	            // hunger 값을 5만큼 감소
	            int newHunger = tamagotchiDto.getHunger() - 5;
	            // hunger 값이 0보다 작거나 100보다 크지 않도록 제한
	            tamagotchiDtoTmp.setHunger(Math.min(Math.max(newHunger, minValue), maxValue)); 
	            tamagotchiDtoTmp.setFatigue(tamagotchiDto.getFatigue());
	            tamagotchiDtoTmp.setHappiness(tamagotchiDto.getHappiness());
	            break;
	        case "sleep":
	            // fatigue 값을 5만큼 감소
	            int newFatigue = tamagotchiDto.getFatigue() - 5;
	            // fatigue 값이 0보다 작거나 100보다 크지 않도록 제한
	            tamagotchiDtoTmp.setHunger(tamagotchiDto.getHunger());
	            tamagotchiDtoTmp.setFatigue(Math.min(Math.max(newFatigue, minValue), maxValue)); 
	            tamagotchiDtoTmp.setHappiness(tamagotchiDto.getHappiness());
	            break;
	        case "play":
	            // fatigue와 happiness 값을 각각 5만큼 증가
	            int newPlayFatigue = tamagotchiDto.getFatigue() + 5;
	            int newPlayHappiness = tamagotchiDto.getHappiness() + 5;
	            // fatigue와 happiness 값이 0보다 작거나 100보다 크지 않도록 제한
	            tamagotchiDtoTmp.setHunger(tamagotchiDto.getHunger());
	            tamagotchiDtoTmp.setFatigue(Math.min(Math.max(newPlayFatigue, minValue), maxValue)); 
	            tamagotchiDtoTmp.setHappiness(Math.min(Math.max(newPlayHappiness, minValue), maxValue)); 
	            break;
	        default:
	            throw new IllegalArgumentException("Invalid action: " + action);  // 잘못된 액션이 들어왔을 경우 예외 던짐
	    }

	    // 업데이트된 값을 DB에 반영
	    tamagochiMapper.updateState(tamagotchiDtoTmp);
	}
	
	@Override
	public void deleteTamagotchi(int tamagotchiId) {
		tamagochiMapper.deleteTamagotchi(tamagotchiId);   
	}

	@Override
	public TamagotchiFileDto selectTamagotchiFileInfo(int imageId, int tamagotchiId) {
		return tamagochiMapper.selectTamagotchiFileInfo(imageId, tamagotchiId);
	}

	@Override
	public void updateDay() {
		tamagochiMapper.updateDay();  
	}


}
