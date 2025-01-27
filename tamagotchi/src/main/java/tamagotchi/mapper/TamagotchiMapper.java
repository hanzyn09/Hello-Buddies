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
	
	void updateState(TamagotchiDto tamagotchiDtoTmp);
	
	void deleteTamagotchi(int tamagotchiId);

	List<TamagotchiFileDto> selectTamagotchiFileList(int tamagotchiId);
	
	TamagotchiFileDto selectTamagotchiFileInfo(@Param("imageId") int imageId, @Param("tamagotchiId") int tamagotchiId);
	
	void updateDay();

	
	
}
