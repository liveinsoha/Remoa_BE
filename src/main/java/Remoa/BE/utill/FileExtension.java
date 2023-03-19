package Remoa.BE.utill;

import org.springframework.web.multipart.MultipartFile;

public class FileExtension {
    public static String fileExtension(MultipartFile multipartFile){
        String fileName = multipartFile.getOriginalFilename();
        assert fileName != null;
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

}
