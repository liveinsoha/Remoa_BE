package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Req.EditProfileForm;
import Remoa.BE.Member.Dto.Res.ResUserInfoDto;
import Remoa.BE.Member.Service.AwsS3Service;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Member.Service.ProfileService;
import Remoa.BE.config.auth.MemberDetails;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseResponse;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.failResponse;
import static Remoa.BE.utill.FileExtension.fileExtension;


@Tag(name = "프로필 기능", description = "프로필 기능 API")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자의 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user")
    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public ResponseEntity<BaseResponse<ResUserInfoDto>> userInfo(@AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        // 로그인된 사용자의 정보를 db에서 다시 불러와 띄워줌.
        Member member = memberService.findOne(memberId);
        ResUserInfoDto resUserInfoDto = ResUserInfoDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .university(member.getUniversity())
                .oneLineIntroduction(member.getOneLineIntroduction())
                .build();

        BaseResponse<ResUserInfoDto> response = new BaseResponse<>(CustomMessage.OK, resUserInfoDto);
        return ResponseEntity.ok(response);
        //      return successResponse(CustomMessage.OK, resUserInfoDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자의 프로필 정보 수정 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/user")
    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.")
    public ResponseEntity<Object> editProfile(@RequestBody EditProfileForm form, @AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();

        Member myMember = memberService.findOne(memberId);
        log.info(myMember.getNickname());
        if (memberService.isNicknameDuplicate(myMember.getNickname())) {

            // 사용자의 입력 정보를 DTO에 담아 서비스로 전달
            profileService.editProfile(memberId, form);
            ResUserInfoDto resUserInfoDto = ResUserInfoDto.builder()
                    .email(myMember.getEmail())
                    .nickname(myMember.getNickname())
                    .phoneNumber(myMember.getPhoneNumber())
                    .university(myMember.getUniversity())
                    .oneLineIntroduction(myMember.getOneLineIntroduction())
                    .build();
            BaseResponse<ResUserInfoDto> response = new BaseResponse<>(CustomMessage.OK, resUserInfoDto);
            return ResponseEntity.ok(response);
            //return successResponse(CustomMessage.OK, resUserInfoDto);
        }

        // 수정이 완료되면 프로필 페이지로 이동
        return

                errorResponse(CustomMessage.UNAUTHORIZED);
    }


    // 프로필 사진 불러오기
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자의 프로필 사진 URL을 조회 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/img")
    @Operation(summary = "프로필 사진 조회", description = "현재 로그인한 사용자의 프로필 사진 URL을 조회합니다.")
    public ResponseEntity<Object> showImage(@AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        BaseResponse<String> response = new BaseResponse<>(CustomMessage.OK, myMember.getProfileImage());
        return ResponseEntity.ok(response);
        //return successResponse(CustomMessage.OK, myMember.getProfileImage());
    }

    // 프로필 사진 업로드
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자의 프로필 사진을 업로드 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/user/img")
    @Operation(summary = "프로필 사진 업로드", description = "현재 로그인한 사용자의 프로필 사진을 업로드합니다.")
    public ResponseEntity<Object> upload(@RequestPart("file") MultipartFile multipartFile, @AuthenticationPrincipal MemberDetails memberDetails) throws IOException {

        // 확장자는 jpg, png만 가능
        String extension = fileExtension(multipartFile);
        if (!"png".equals(extension) && !"jpg".equals(extension)) {
            return failResponse(CustomMessage.BAD_FILE
                    , "이미지 파일은 jpg, png만 지원합니다. 현재 이미지 파일은 " + extension + "입니다");
        }

        // 이미지 용량 체크
        if (PROFILE_IMG_MAX_SIZE < multipartFile.getSize()) {
            return failResponse(CustomMessage.FILE_SIZE_OVER, "프로필 사진은 2MB를 초과할 수 없습니다.");
        }

        // 이미지 사이즈(픽셀) 체크
        Map<String, Integer> imageSizeMap = checkImageSize(multipartFile); // 필요시 IOException 핸들링 할 것
        int widthPixel = imageSizeMap.get("width");
        int heightPixel = imageSizeMap.get("height");
        if (widthPixel < PROFILE_IMG_MIN_WIDTH_PIXEL || heightPixel < PROFILE_IMG_MIN_HEIGHT_PIXEL) {
            return failResponse(CustomMessage.IMAGE_PIXEL_LACK
                    , "가로/세로 110픽셀 이상만 가능합니다. 현재 가로 : " + widthPixel + " / 세로 : " + heightPixel + "입니다.");
        }

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);

        String editProfileImg = awsS3Service.editProfileImg(myMember.getProfileImage(), multipartFile);
        myMember.setProfileImage(editProfileImg);
        memberService.join(myMember);

        return new ResponseEntity<>(HttpStatus.OK);

    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자의 프로필 사진을 삭제 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/user/img")
    @Operation(summary = "프로필 사진 삭제", description = "현재 로그인한 사용자의 프로필 사진을 삭제합니다.")
    public ResponseEntity<Object> remove(@AuthenticationPrincipal MemberDetails memberDetails) throws MalformedURLException {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);

        //유저가 기본프로필이 아니라면
        if (!Objects.equals(myMember.getProfileImage(), "https://remoafiles.s3.ap-northeast-2.amazonaws.com/img/profile_img.png")) {
            awsS3Service.removeProfileUrl(myMember.getProfileImage());
            myMember.setProfileImage("https://remoafiles.s3.ap-northeast-2.amazonaws.com/img/profile_img.png");
            memberService.join(myMember);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }


    /**
     * 프론트에서 닉네임 중복 검사를 할 때 사용할 메서드
     *
     * @param nickname
     * @return ResponseEntity
     */

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 사용 중인지 확인 성공"),
    })
    @GetMapping("/nickname")
    @Operation(summary = "닉네임 중복 확인", description = "사용자가 입력한 닉네임이 이미 사용 중인지 확인합니다.")
    public ResponseEntity<BaseResponse<Boolean>> checkNicknameDuplicate(@RequestParam String nickname) {
        if (memberService.isNicknameDuplicate(nickname)) {
            BaseResponse<Boolean> response = new BaseResponse<>(CustomMessage.OK, false);
            return ResponseEntity.ok(response);
            //  return successResponse(CustomMessage.OK, false);
        } else {
            BaseResponse<Boolean> response = new BaseResponse<>(CustomMessage.OK, true);
            return ResponseEntity.ok(response);
            //   return successResponse(CustomMessage.OK, true);
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