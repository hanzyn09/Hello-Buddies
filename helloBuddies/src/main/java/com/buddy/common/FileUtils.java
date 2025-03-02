package com.buddy.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.buddy.dto.BuddyFileDto;
import com.buddy.service.BuddyServiceImpl;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class FileUtils {
    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;
    
    // 요청을 통해서 전달받은 파일을 저장하고, 파일 정보를 반환하는 메서드 
    public List<BuddyFileDto> parseFileInfo(int BuddyId, MultipartHttpServletRequest request) throws Exception {
        if (ObjectUtils.isEmpty(request)) {
            return null;
        }
        
        List<BuddyFileDto> fileInfoList = new ArrayList<>();
        
        // 파일을 저장할 디렉터리를 설정
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime now = ZonedDateTime.now();
        String storedDir = uploadDir + "images\\" + now.format(dtf);  
        File fileDir = new File(storedDir);
       
        // 디렉터리가 없으면 생성
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        // 업로드된 파일들을 디렉터리에 저장하고 정보 리스트에 저장 
        Iterator<String> fileTagNames = request.getFileNames();
        while(fileTagNames.hasNext()) {
  
            String fileTagName = fileTagNames.next();
            List<MultipartFile> files = request.getFiles(fileTagName);
            for (MultipartFile file : files) {
                String originalFileExtension = "";
                
                // 파일 확장자 처리
                if (!file.isEmpty()) {
                    String contentType = file.getContentType();
                    if (ObjectUtils.isEmpty(contentType)) {
                        break;
                    } else {
                        if (contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        } else if (contentType.contains("image/png")) {
                            originalFileExtension = ".png";
                        } else if (contentType.contains("image/gif")) {
                            originalFileExtension = ".gif";
                        } else {
                            break;
                        }
                    }
                    
                    // 저장할 파일 이름 조합
                    String storedFileName = Long.toString(System.nanoTime()) + originalFileExtension;
                    String storedFilePath = storedDir + "\\" + storedFileName;
                    String imageUrl = "/images/" + now.format(dtf) + "/" + storedFileName; // 클라이언트에게 전달할 URL
                    
                    // 파일 정보를 리스트에 저장 
                    BuddyFileDto dto = new BuddyFileDto();
                    dto.setBuddyId(BuddyId);
                    dto.setImageUrl(imageUrl);
                    dto.setFileSize(Long.toString(file.getSize()));
                    dto.setOriginalFileName(file.getOriginalFilename());
                    dto.setStoredFilePath(storedFilePath);
                    
                    fileInfoList.add(dto);
  
                    // 파일 저장
                    fileDir = new File(storedFilePath);
                    
                    // 두 경로에 파일 저장
                    file.transferTo(fileDir);

                }
            }
        }      
        return fileInfoList;
    }
}
