package com.buddy.dto;

import java.util.List;

import lombok.Data;

@Data
public class BuddyDto {

    private int buddyId;        // 타마고치의 고유 ID
    private String name;             // 타마고치의 이름
    private int levelNumber;		 // 레벨
    private int hunger;              // 배고픔 상태
    private int fatigue;             // 피로도 상태
    private int happiness;           // 행복도 상태
    private String createDt;           // 행복도 상태
    
    // 첨부 파일 정보를 저장할 필드를 추가
    private List<BuddyFileDto> fileInfoList;
}