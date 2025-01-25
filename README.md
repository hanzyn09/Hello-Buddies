# tamagotchi-springboot
with SpringBoot, Thymeleaf, MySQL


# Database Schema for Tamagotchi

This document provides the SQL queries for creating the required tables to store Tamagotchi data. The schema consists of two tables: `tamagotchis_M` and `tamagotchis_F`.

## 1. `tamagotchis_M` Table

The `tamagotchis_M` table is the main table that stores information about each Tamagotchi. It includes attributes like the Tamagotchi's name, hunger, fatigue, happiness, and timestamps for when the record was created or updated.

```sql
CREATE TABLE tamagotchis_M (
    tamagotchi_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    hunger INT DEFAULT 50,
    fatigue INT DEFAULT 50,
    happiness INT DEFAULT 50,
    create_dt DATETIME NOT NULL COMMENT '작성 일시',
    update_dt DATETIME NULL COMMENT '수정 시간',
    deleted_yn VARCHAR(2) NOT NULL DEFAULT 'N'
);
```


## 2. `tamagotchis_F` Table

The `tamagotchis_F` table stores image-related information for each Tamagotchi. This table includes details about the image URL, the original file name, file path, file size, and timestamps for when the record was created or updated.

```sql
CREATE TABLE tamagotchis_F (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY,     
    tamagotchi_id INT NOT NULL,                         
    image_url VARCHAR(500) NOT NULL,                    -- 이미지 URL
    original_file_name VARCHAR(255) NOT NULL COMMENT '원본 파일 이름',  -- 원본 파일 이름
    stored_file_path VARCHAR(500) NOT NULL COMMENT '파일 저장 경로',    -- 파일 저장 경로
    file_size INT(15) UNSIGNED NOT NULL COMMENT '파일 크기',  -- 파일 크기
    create_dt DATETIME NOT NULL COMMENT '작성 일시',
    update_dt DATETIME NULL COMMENT '수정 시간',
    FOREIGN KEY (tamagotchi_id) REFERENCES tamagotchis_M(tamagotchi_id) ON DELETE CASCADE  -- tamagotchis_M 테이블의 tamagotchi_id를 참조
);
```
