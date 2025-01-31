# Hello Buddies 프로젝트

이 프로젝트는 다마고치(Tamagotchi) 스타일의 **Hello Buddies** 웹 애플리케이션입니다.<br>
플레이어는 **Hello Buddies** 캐릭터를 키우고, 관리하며, 다양한 상호작용을 할 수 있습니다.<br>
캐릭터는 배고픔, 피로, 행복도를 관리할 수 있으며, 다양한 이미지 파일을 업로드할 수 있습니다.

## 기술 스택

- **Spring Boot**: 백엔드 서버 프레임워크
- **JDK 21**: Java 21을 사용하여 개발
- **MySQL**: 데이터베이스 관리 시스템
- **Thymeleaf**: HTML 템플릿 엔진
- **HikariCP**: 데이터베이스 연결 풀

## 기능
- **캐릭터 생성 및 관리**: 사용자는 `Hello Buddies` 캐릭터를 생성하고, 캐릭터의 이름을 지정할 수 있습니다.
- **상태 관리**: 캐릭터는 배고픔, 피로도, 행복도 등의 상태를 실시간으로 관리할 수 있습니다. 상태가 변화함에 따라 캐릭터가 다르게 반응합니다.
- **프로필 사진 업/다운로드**: 사용자는 캐릭터의 프로필 사진을 업로드할 수 있으며, 업로드된 이미지는 상세 보기를 통해 확인 후 다운로드 할 수 있습니다.
- **레벨 관리**: 캐릭터의 레벨을 올리고, 레벨에 따라 캐릭터의 능력치가 향상됩니다.
- **데이터베이스 연동**: MySQL 데이터베이스와 연동하여 캐릭터 및 이미지 데이터를 저장하고 관리합니다.
- **상호작용 기능**: 캐릭터와 다양한 상호작용을 통해 배고픔, 피로, 행복도를 관리할 수 있으며, 이를 통해 캐릭터의 상태가 변화합니다.

## 설치 및 실행 방법

### 1. JAR 파일 다운로드

JAR 파일을 다운로드하려면, [**Hello Buddies Release 0.0.1**](https://github.com/hanzyn09/hello-buddies/releases/tag/0.0.1) 링크에서 최신 버전을 다운로드하십시오.

### 2. MySQL 데이터베이스 설정

이 프로젝트는 MySQL 데이터베이스를 사용합니다. 다음 정보를 바탕으로 데이터베이스와 테이블을 설정해야 합니다.

#### 데이터베이스 설정

1. MySQL 서버가 설치되어 있어야 합니다.
2. 아래 SQL 명령어를 사용하여 `springbootdb` 데이터베이스와 두 개의 테이블(`buddies_M`과 `buddies_F`)을 생성하십시오.
- `buddies_M` : 캐릭터에 대한 기본 정보를 저장합니다.
- `buddies_F` : 캐릭터의 프로필 사진 파일 정보를 저장합니다.

```sql
CREATE DATABASE springbootdb;  -- 데이터베이스 생성

USE springbootdb;  -- 데이터베이스 선택

-- buddies_M 테이블 생성
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

-- buddies_F 테이블 생성
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

### 3. `src/main/resources/application.properties` 파일을 열어 아래와 같이 수정합니다.

#### 기본 설정

```properties
# MySQL 데이터베이스 연결 설정
spring.datasource.url=jdbc:mysql://localhost:3306/springbootdb?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Seoul
spring.datasource.username=springboot  # MySQL 사용자명
spring.datasource.password=p@ssw0rd   # MySQL 비밀번호
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate 설정 (자동 스키마 업데이트)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true  # SQL 쿼리 로그 출력
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# 서버 포트 설정
server.port=8080  # 기본 포트 설정 (필요에 따라 변경 가능)
```
이 외에도 다양한 설정이 가능하며, 필요에 따라 `application.properties` 파일을 조정하여 환경에 맞게 최적화할 수 있습니다.

### 4. JAR 파일 실행
JAR 파일을 다운로드한 후, 설치 위치로 이동하여 아래 명령어로 애플리케이션을 실행할 수 있습니다:
```bash
java -jar helloBuddies-0.0.1-SNAPSHOT.jar
```
애플리케이션이 성공적으로 실행되면, 기본적으로 http://localhost:8080 에서 접속하여 애플리케이션을 사용할 수 있습니다.

### 5. 웹 애플리케이션 사용
웹 브라우저에서 `http://localhost:8080`에 접속하여 **Hello Buddies** 애플리케이션을 사용할 수 있습니다.<br>
이 애플리케이션에서 캐릭터를 생성하고 관리하며, 다양한 상호작용을 할 수 있습니다.
