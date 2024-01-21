package Remoa.BE.Member.Service;

import Remoa.BE.Member.Dto.Req.EditProfileForm;
import Remoa.BE.Notice.Service.InquiryService;
import Remoa.BE.Notice.Service.NoticeService;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import Remoa.BE.Member.Domain.Member;
import org.springframework.util.FileCopyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final MemberService memberService;
    private final InquiryService inquiryService;
    private final NoticeService noticeService;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    @Transactional
    public void editProfile(Long memberId, EditProfileForm profile) {
        Member member = memberService.findOne(memberId);

        String oldNickname = member.getNickname();
        String newNickname = profile.getNickname();
        //공지사항 및 문의사항에 변경된 닉네임 반영
        noticeService.modifying_Notice_NickName(oldNickname, newNickname);
        inquiryService.modifying_Inquiry_NickName(oldNickname, newNickname);

        // 사용자의 프로필 정보 수정
        member.setNickname(profile.getNickname());
        member.setPhoneNumber(profile.getPhoneNumber());
        member.setUniversity(profile.getUniversity());
        member.setOneLineIntroduction(profile.getOneLineIntroduction());
    }

    public String editProfileImg(String nickname,String profileImageUrl) throws IOException {
        // 프로필사진을 jpg로 변환하기
        URL profileURL = new URL(profileImageUrl);
        InputStream is = profileURL.openStream();

        // 이미지 파일 생성
        BufferedImage image = ImageIO.read(is);
        File outputFile = new File(nickname+".jpg");
        ImageIO.write(image, "jpg", outputFile);

        // 사진으로 바꾼뒤 바로 S3로 업로드하기
        String url = uploadProfileImg(outputFile);
        outputFile.delete();
        return url;
    }

    // 프로필 사진 초기설정 - S3에 저장하기
    public String uploadProfileImg(File file){
        String s3FileName = UUID.randomUUID() + "-" + file.getName();
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(file.length());
        amazonS3.putObject(bucket, "img/"+s3FileName,file);
        return amazonS3.getUrl(bucket,"img/"+s3FileName).toString().replaceAll("\\+", "+");

    }
}