# Hello Buddies 프로젝트

이 프로젝트는 다마고치(Tamagotchi) 스타일의 **Hello Buddies** 웹 애플리케이션입니다. 플레이어는 **Hello Buddies** 캐릭터를 키우고, 관리하며, 다양한 상호작용을 할 수 있습니다. 캐릭터는 배고픔, 피로, 행복도를 관리할 수 있으며, 다양한 이미지 파일을 업로드할 수 있습니다.

## 기술 스택

- **Spring Boot**: 백엔드 서버 프레임워크
- **JDK 21**: Java 21을 사용하여 개발
- **MySQL**: 데이터베이스 관리 시스템
- **Thymeleaf**: HTML 템플릿 엔진
- **HikariCP**: 데이터베이스 연결 풀

## 데이터베이스 테이블

이 프로젝트는 MySQL 데이터베이스를 사용하며, 두 개의 주요 테이블인 `buddies_M`과 `buddies_F`가 있습니다.

### buddies_M 테이블

`buddies_M` 테이블은 각 **Hello Buddies** 캐릭터에 대한 기본 정보를 저장합니다.

```sql
CREATE TABLE buddies_M (
    buddy_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    level_number INT DEFAULT 1,
    hunger INT DEFAULT 50,
    fatigue INT DEFAULT 50,
    happiness INT DEFAULT 50,
    create_dt DATETIME NOT NULL COMMENT '작성 일시',
    update_dt DATETIME NULL COMMENT '수정 시간',
    deleted_yn VARCHAR(2) NOT NULL DEFAULT 'N'
);
```
### buddies_F 테이블
`buddies_F` 테이블은 캐릭터와 연결된 이미지 파일 정보를 저장합니다.
```sql
CREATE TABLE buddies_F (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY,     
    buddy_id INT NOT NULL,                        		
    image_url VARCHAR(500) NOT NULL,                    -- 이미지 URL
    original_file_name VARCHAR(255) NOT NULL COMMENT '원본 파일 이름',  -- 원본 파일 이름
    stored_file_path VARCHAR(500) NOT NULL COMMENT '파일 저장 경로',    -- 파일 저장 경로
    file_size INT(15) UNSIGNED NOT NULL COMMENT '파일 크기',  -- 파일 크기
    create_dt DATETIME NOT NULL COMMENT '작성 일시',
    update_dt DATETIME NULL COMMENT '수정 시간',
    FOREIGN KEY (buddy_id) REFERENCES buddies_M(buddy_id) ON DELETE CASCADE  -- buddies_M 테이블의 buddy_id를 참조
);
```

## 기능
- **캐릭터 생성 및 관리**: 사용자는 `Hello Buddies` 캐릭터를 생성하고, 캐릭터의 이름을 지정할 수 있습니다.
- **상태 관리**: 캐릭터는 배고픔, 피로도, 행복도 등의 상태를 실시간으로 관리할 수 있습니다. 상태가 변화함에 따라 캐릭터가 다르게 반응합니다.
- **이미지 업로드**: 사용자는 캐릭터와 관련된 이미지를 업로드할 수 있으며, 업로드된 이미지의 정보는 `buddies_F` 테이블에 저장됩니다.
- **레벨 관리**: 캐릭터의 레벨을 올리고, 레벨에 따라 캐릭터의 능력치가 향상됩니다.
- **데이터베이스 연동**: MySQL 데이터베이스와 연동하여 캐릭터 및 이미지 데이터를 저장하고 관리합니다.
- **상호작용 기능**: 캐릭터와 다양한 상호작용을 통해 배고픔, 피로, 행복도를 관리할 수 있으며, 이를 통해 캐릭터의 상태가 변화합니다.

## 설치 및 실행 방법
