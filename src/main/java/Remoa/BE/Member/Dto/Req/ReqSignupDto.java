package Remoa.BE.Member.Dto.Req;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ReqSignupDto {

    @NotBlank(message = "이메일은 필수값입니다.")
    private String email;

    @NotNull(message = "카카오에서 발급받은 id값이 누락되었습니다.")
    private Long kakaoId;


    @NotNull(message = "선택 동의사항 값은 필수입니다.")
    private Boolean termConsent;
}