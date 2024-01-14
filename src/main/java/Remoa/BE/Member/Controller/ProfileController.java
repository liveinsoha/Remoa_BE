package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.AwsS3;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Req.EditProfileForm;
import Remoa.BE.Member.Dto.Res.ResUserInfoDto;
//import Remoa.BE.Member.Service.ImageService;
import Remoa.BE.Member.Service.AwsS3Service;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Member.Service.ProfileService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import static Remoa.BE.exception.CustomBody.*;
import static Remoa.BE.utill.FileExtension.fileExtension;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final MemberService memberService;
    private final AwsS3Service awsS3Service;
    private static final long PROFILE_IMG_MAX_SIZE = 2097152L;
    private static final int PROFILE_IMG_MIN_WIDTH_PIXEL = 110;
    private static final int PROFILE_IMG_MIN_HEIGHT_PIXEL = 110;
    // 프로필 수정 범위 : 닉네임(중복확인), 핸드폰번호, 대학교, 한줄소개
    @GetMapping("/user")
    public ResponseEntity<Object> userInfo(HttpServletRequest request) {

        if(authorized(request)) {
            Long memberId = getMemberId();
            // 로그인된 사용자의 정보를 db에서 다시 불러와 띄워줌.
            Member member = memberService.findOne(memberId);
            ResUserInfoDto resUserInfoDto = ResUserInfoDto.builder()
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .phoneNumber(member.getPhoneNumber())
                    .university(member.getUniversity())
                    .oneLineIntroduction(member.getOneLineIntroduction())
                    .build();
            return successResponse(CustomMessage.OK, resUserInfoDto);
        }

        return errorResponse(CustomMessage.UNAUTHORIZED);

    }

    @PutMapping("/user")
    public ResponseEntity<Object> editProfile(@RequestBody EditProfileForm form, HttpServletRequest request) {

        if(authorized(request)){
            Long memberId = getMemberId();

            Member myMember = memberService.findOne(memberId);
            log.info(myMember.getNickname());
            if(memberService.isNicknameDuplicate(myMember.getNickname())) {

                // 사용자의 입력 정보를 DTO에 담아 서비스로 전달
                profileService.editProfile(memberId, form);
                ResUserInfoDto resUserInfoDto = ResUserInfoDto.builder()
                        .email(myMember.getEmail())
                        .nickname(myMember.getNickname())
                        .phoneNumber(myMember.getPhoneNumber())
                        .university(myMember.getUniversity())
                        .oneLineIntroduction(myMember.getOneLineIntroduction())
                        .build();

                return successResponse(CustomMessage.OK, resUserInfoDto);
            }

            return errorResponse(CustomMessage.BAD_DUPLICATE);

        }

        // 수정이 완료되면 프로필 페이지로 이동
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }


    // 프로필 사진 불러오기
    @GetMapping("/user/img")
    public ResponseEntity<Object> showImage(HttpServletRequest request) {
        if(authorized(request)) {
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            return successResponse(CustomMessage.OK, myMember.getProfileImage());
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    // 프로필 사진 업로드
    @PutMapping("/user/img")
    public ResponseEntity<Object> upload(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) throws IOException {

        if(authorized(request)) {

            // 확장자는 jpg, png만 가능
            String extension = fileExtension(multipartFile);
            if(!"png".equals(extension) && !"jpg".equals(extension)) {
                return failResponse(CustomMessage.BAD_FILE
                        , "이미지 파일은 jpg, png만 지원합니다. 현재 이미지 파일은 " + extension + "입니다");
            }

            // 이미지 용량 체크
            if(PROFILE_IMG_MAX_SIZE < multipartFile.getSize()){
                return failResponse(CustomMessage.FILE_SIZE_OVER, "프로필 사진은 2MB를 초과할 수 없습니다.");
            }

            // 이미지 사이즈(픽셀) 체크
            Map<String, Integer> imageSizeMap = checkImageSize(multipartFile); // 필요시 IOException 핸들링 할 것
            int widthPixel = imageSizeMap.get("width");
            int heightPixel = imageSizeMap.get("height");
            if(widthPixel < PROFILE_IMG_MIN_WIDTH_PIXEL || heightPixel < PROFILE_IMG_MIN_HEIGHT_PIXEL) {
                return failResponse(CustomMessage.IMAGE_PIXEL_LACK
                        , "가로/세로 110픽셀 이상만 가능합니다. 현재 가로 : " + widthPixel + " / 세로 : " + heightPixel + "입니다.");
            }

            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);

            String editProfileImg = awsS3Service.editProfileImg(myMember.getProfileImage(), multipartFile);
            myMember.setProfileImage(editProfileImg);
            memberService.join(myMember);

            return new ResponseEntity<>(HttpStatus.OK);

        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @DeleteMapping("/user/img")
    public ResponseEntity<Object> remove(HttpServletRequest request) throws MalformedURLException {
        if(authorized(request)) {

            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);

            //유저가 기본프로필이 아니라면
            if(!Objects.equals(myMember.getProfileImage(), "https://remoafiles.s3.ap-northeast-2.amazonaws.com/img/profile_img.png")) {
                awsS3Service.removeProfileUrl(myMember.getProfileImage());
                myMember.setProfileImage("https://remoafiles.s3.ap-northeast-2.amazonaws.com/img/profile_img.png");
                memberService.join(myMember);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }




    /**
     * 프론트에서 닉네임 중복 검사를 할 때 사용할 메서드
     * @param nickname
     * @return ResponseEntity
     */
    @GetMapping("/nickname")
    public ResponseEntity<Object> checkNicknameDuplicate(@RequestParam String nickname) {
        if (memberService.isNicknameDuplicate(nickname)) {
            return successResponse(CustomMessage.OK, false);
        } else {
            return successResponse(CustomMessage.OK, true);
        }
    }

    private Map<String, Integer> checkImageSize(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        log.warn("width  = " + width);
        log.warn("height = " + height);

        Map<String, Integer> imageSizeMap = new HashMap<>();
        imageSizeMap.put("width", width);
        imageSizeMap.put("height", height);
        return imageSizeMap;
    }
}