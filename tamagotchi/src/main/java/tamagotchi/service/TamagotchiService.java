package tamagotchi.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import tamagotchi.dto.TamagotchiDto;
import tamagotchi.dto.TamagotchiFileDto;

public interface TamagotchiService {

	List<TamagotchiDto> selectTamagotchiList();

	void createTamagotchi(String name, MultipartHttpServletRequest request);

	TamagotchiDto selectTamagotchiDetail(int tamagotchiId);
	
	void updatePlay(int tamagotchiId);

	void updateHunger(int tamagotchiId);

	void updateSleep(int tamagotchiId);

	void deleteTamagotchi(int tamagotchiId);

	void updateDay();

	TamagotchiFileDto selectTamagotchiFileInfo(int imageId, int tamagotchiId);

}
