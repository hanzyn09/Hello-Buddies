package tamagotchi.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import tamagotchi.dto.TamagotchiDto;
import tamagotchi.dto.TamagotchiFileDto;

public interface TamagotchiService {

	List<TamagotchiDto> selectTamagotchiList();

	void createTamagotchi(String name, MultipartHttpServletRequest request);

	TamagotchiDto selectTamagotchiDetail(int tamagotchiId);
	
	void updateState(int tamagotchiId, String action);
	
	void deleteTamagotchi(int tamagotchiId);

	TamagotchiFileDto selectTamagotchiFileInfo(int imageId, int tamagotchiId);
	
	void updateDay();

}
