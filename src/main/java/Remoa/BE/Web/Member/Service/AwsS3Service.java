package Remoa.BE.Web.Member.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String editProfileImg(String profileImgUrl,MultipartFile multipartFile) throws IOException {

        if(!Objects.equals(profileImgUrl, "https://remoafiles.s3.ap-northeast-2.amazonaws.com/img/profile_img.png")) {
            //기존 프로필 사진 s3에서 삭제
            removeProfileUrl(profileImgUrl);
        }

        //파일 타입과 사이즈 저장
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpeg");
        log.info(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        //파일 이름
        String originalFilename = multipartFile.getOriginalFilename();

        //파일 이름이 겹치지 않게
        String uuid = UUID.randomUUID().toString();

        //post 폴더에 따로 넣어서 보관
        String s3name = "img/"+uuid+"_"+originalFilename;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, s3name, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            //파일을 제대로 받아오지 못했을때
            //Todo 예외처리 custom 따로 만들기
            throw new RuntimeException(e);
        }

        return amazonS3.getUrl(bucket,s3name).toString().replaceAll("\\+", "+");

    }

    public void removeProfileUrl(String profileImgUrl) throws MalformedURLException {
        URL fileUrl = new URL(profileImgUrl);
        String objectKey = fileUrl.getPath().replaceAll("^/", "");
        amazonS3.deleteObject(bucket,objectKey);
    }
}