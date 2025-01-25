package tamagotchi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import tamagotchi.dto.TamagotchiDto;
import tamagotchi.dto.TamagotchiFileDto;

@Mapper
public interface TamagotchiMapper {
	List<TamagotchiDto> selectTamagotchiList();

	void insertTamagotchi(TamagotchiDto tamagotchiDto);

	void insertTamagotchiFileList(List<TamagotchiFileDto> fileInfoList);

	TamagotchiDto selectTamagotchiDetail(int tamagotchiId);
	
	void updatePlay(int tamagotchiId);

	void updateHunger(int tamagotchiId);

	void updateSleep(int tamagotchiId);

	void deleteTamagotchi(int tamagotchiId);

	void updateDay();
	
	List<TamagotchiFileDto> selectTamagotchiFileList(int tamagotchiId);
	
	TamagotchiFileDto selectTamagotchiFileInfo(@Param("imageId") int imageId, @Param("tamagotchiId") int tamagotchiId);

}
