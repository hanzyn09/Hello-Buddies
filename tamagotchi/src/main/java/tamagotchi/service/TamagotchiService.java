package tamagotchi.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import tamagotchi.dto.TamagotchiDto;
import tamagotchi.dto.TamagotchiFileDto;

public interface TamagotchiService {

	List<TamagotchiDto> selectTamagotchiList();

	String createTamagotchi(String name, MultipartHttpServletRequest request);

	TamagotchiDto selectTamagotchiDetail(int tamagotchiId);
	
	String updateState(int tamagotchiId, String action);
	
	String deleteTamagotchi(int tamagotchiId);

	TamagotchiFileDto selectTamagotchiFileInfo(int imageId, int tamagotchiId);

	String updateDate(String action);
}
