package tamagotchi.dto;

import lombok.Data;

@Data
public class TamagotchiFileDto {

    private long imageId;      // 이미지 ID
    private int tamagotchiId;  // 다마고치 ID (외래 키)
    private String imageUrl;   // 이미지 URL
    private String originalFileName;
    private String storedFilePath;
    private String fileSize;
}