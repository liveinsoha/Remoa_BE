package Remoa.BE.Member.Dto.Res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResSignupDto {

    @Schema(description = "카카오 아이디", example = "2670589")
    private Long kakaoId;

    @Schema(description = "이메일", example = "biba99@naver.com")
    private String email;

    @Schema(description = "닉네임", example = "오민택")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://remoa.s3.ap-northeast-2.amazonaws.com/img/7e8b62e7-4fbe-4039-a084-8a01a08ee35b-%EC%98%A4%EB%AF%BC%ED%83%9D.jpg")
    private String profileImage;

    @Schema(description = "이용약관 동의 여부", example = "true")
    private boolean termConsent;
}
