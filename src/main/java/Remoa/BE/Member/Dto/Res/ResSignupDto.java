package Remoa.BE.Member.Dto.Res;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
public class ResSignupDto {

    private Long kakaoId;
    private String email;

    private String nickname;

    private String profileImage;

    private boolean termConsent;
}
