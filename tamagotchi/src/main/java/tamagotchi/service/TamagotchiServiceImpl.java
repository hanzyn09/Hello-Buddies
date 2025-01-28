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

	@Override
	public List<TamagotchiDto> selectTamagotchiList() {
		return tamagochiMapper.selectTamagotchiList();
	}

	/*
	 * @Scheduled(fixedRate = 10000) // 10초마다 실행 (밀리초 단위) public void
	 * autoUpdateTamagotchi() { List<TamagotchiDto> tamagotchiList =
	 * selectTamagotchiList(); if (tamagotchiList != null &&
	 * !tamagotchiList.isEmpty()) { //타마고치 상태 자동 업데이트 tamagochiMapper.updateDay(); }
	 * }
	 */
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
			List<TamagotchiFileDto> fileInfoList = fileUtils.parseFileInfo(tamagotchiDto.getTamagotchiId(), request);
			// 첨부 파일 정보를 DB에 저장 (이미지가 있을 경우)
			if (!CollectionUtils.isEmpty(fileInfoList)) {
				tamagochiMapper.insertTamagotchiFileList(fileInfoList);
			}
		} catch (Exception e) {
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

		// 기존값 셋팅
		int oldLevelNumber = tamagotchiDto.getLevelNumber();
		int oldHunger = tamagotchiDto.getHunger();
		int oldFatigue = tamagotchiDto.getFatigue();
		int oldHappiness = tamagotchiDto.getHappiness();

		tamagotchiDtoTmp.setTamagotchiId(tamagotchiId);

		// 액션에 따른 상태 업데이트
		updateTamagotchiState(action, oldLevelNumber, oldHunger, oldFatigue, oldHappiness, tamagotchiDtoTmp);

		// 업데이트된 값을 DB에 반영
		tamagochiMapper.updateState(tamagotchiDtoTmp);
	}

	private void updateTamagotchiState(String action, int oldLevelNumber, int oldHunger, int oldFatigue,
										int oldHappiness, TamagotchiDto tamagotchiDtoTmp) {
		final int minValue = 0;
		final int maxValue = 100;

		switch (action) {
		case "hunger":
			int newHunger = Math.min(Math.max(oldHunger - 5, minValue), maxValue);
			
			if (newHunger == 0 && oldFatigue == 0 && oldHappiness == 100) {
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
				newHunger = 50;
				oldFatigue = 50;
				oldHappiness = 50;
			} else 
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber);
			
			tamagotchiDtoTmp.setHunger(newHunger);
			tamagotchiDtoTmp.setFatigue(oldFatigue);
			tamagotchiDtoTmp.setHappiness(oldHappiness);
			break;

		case "sleep":
			int newFatigue = Math.min(Math.max(oldFatigue - 5, minValue), maxValue);
			
			if (oldHunger == 0 && newFatigue == 0 && oldHappiness == 100) {
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
				oldHunger = 50;
				newFatigue = 50;
				oldHappiness = 50;
			} else 
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber);
			
			tamagotchiDtoTmp.setHunger(oldHunger);
			tamagotchiDtoTmp.setFatigue(newFatigue);
			tamagotchiDtoTmp.setHappiness(oldHappiness);
			break;

		case "play":
			int newPlayFatigue = Math.min(Math.max(oldFatigue + 5, minValue), maxValue);
			int newPlayHappiness = Math.min(Math.max(oldHappiness + 5, minValue), maxValue);
			
			if (oldHunger == 0 && newPlayFatigue == 0 && newPlayHappiness == 100) {
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
				oldHunger = 50;
				newPlayFatigue = 50;
				newPlayHappiness = 50;
			} else 
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber);
			
			tamagotchiDtoTmp.setHunger(oldHunger);
			tamagotchiDtoTmp.setFatigue(newPlayFatigue);
			tamagotchiDtoTmp.setHappiness(newPlayHappiness);
			break;
			
		case "day":
			int newDayHunger = Math.min(Math.max(oldHunger + 10, minValue), maxValue);
			int newDayFatigue = Math.min(Math.max(oldFatigue + 10, minValue), maxValue);
			int newDayHappiness = Math.min(Math.max(oldHappiness - 10, minValue), maxValue);

			if (newDayHunger == 0 && newDayFatigue == 0 && newDayHappiness == 100) {
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber + 1);
				newDayHunger = 50;
				newDayFatigue = 50;
				newDayHappiness = 50;
			} else
				tamagotchiDtoTmp.setLevelNumber(oldLevelNumber);

			tamagotchiDtoTmp.setHunger(newDayHunger);
			tamagotchiDtoTmp.setFatigue(newDayFatigue);
			tamagotchiDtoTmp.setHappiness(newDayHappiness);
			break;
		default:
			throw new IllegalArgumentException("Invalid action: " + action);
		}
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
	public void updateDate(String action) {
		List<TamagotchiDto> list = this.selectTamagotchiList();

		for (TamagotchiDto tamagotchi : list) {
			TamagotchiDto tamagotchiDto = this.selectTamagotchiDetail(tamagotchi.getTamagotchiId());
			TamagotchiDto tamagotchiDtoTmp = new TamagotchiDto();

			tamagotchiDtoTmp.setTamagotchiId(tamagotchiDto.getTamagotchiId());
			
			 updateTamagotchiState(action, tamagotchiDto.getLevelNumber(), tamagotchiDto.getHunger(),
	                    tamagotchiDto.getFatigue(), tamagotchiDto.getHappiness(), tamagotchiDtoTmp);
			 
			// 업데이트된 값을 DB에 반영
			tamagochiMapper.updateState(tamagotchiDtoTmp);
		}
	}
}
