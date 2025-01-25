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
	public void updatePlay(int tamagotchiId) {		
		tamagochiMapper.updatePlay(tamagotchiId);  
		
	}

	@Override
	public void updateHunger(int tamagotchiId) {
		tamagochiMapper.updateHunger(tamagotchiId);  
		
	}

	@Override
	public void updateSleep(int tamagotchiId) {
		tamagochiMapper.updateSleep(tamagotchiId);  
		
	}

	@Override
	public void updateDay() {
		tamagochiMapper.updateDay();  
	}
	
	@Override
	public void deleteTamagotchi(int tamagotchiId) {
		tamagochiMapper.deleteTamagotchi(tamagotchiId);   
	}

	@Override
	public TamagotchiFileDto selectTamagotchiFileInfo(int imageId, int tamagotchiId) {
		return tamagochiMapper.selectTamagotchiFileInfo(imageId, tamagotchiId);
	}

}
