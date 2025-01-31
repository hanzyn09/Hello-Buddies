package com.buddy.dto;

import lombok.Data;

@Data
public class BuddyFileDto {

    private long imageId;      // 이미지 ID
    private int BuddyId;  // 다마고치 ID (외래 키)
    private String imageUrl;   // 이미지 URL
    private String originalFileName;
    private String storedFilePath;
    private String fileSize;
}