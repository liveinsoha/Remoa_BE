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

import javax.servlet.http.HttpServletRequest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;


import static Remoa.BE.exception.CustomBody.*;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final MemberService memberService;
    private final AwsS3Service awsS3Service;

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

            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);;

            String editProfileImg = awsS3Service.editProfileImg(myMember.getProfileImage(), multipartFile);
            myMember.setProfileImage(editProfileImg);
            memberService.join(myMember);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    // 프로필 사진 삭제
    @DeleteMapping("/user/img")
    public ResponseEntity<Object> remove(HttpServletRequest request) throws MalformedURLException {
        if(authorized(request)) {

            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            if(myMember.getProfileImage() == null){
                return errorResponse(CustomMessage.BAD_PROFILE_IMG);
            }

            awsS3Service.removeProfileUrl(myMember.getProfileImage());
            myMember.setProfileImage(null);
            memberService.join(myMember);
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
            return successResponse(CustomMessage.OK, nickname + "은(는) 이미 사용중인 닉네임입니다.");
        } else {
            return successResponse(CustomMessage.OK, nickname + "은(는) 사용 가능한 닉네임입니다.");
        }
    }
}