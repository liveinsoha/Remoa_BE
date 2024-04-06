package Remoa.BE.Member.Dto.Res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class KakaoLoginResponseDto {

    @Schema(description = "JWT 인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QWNjb3VudCIsImFjY291bnQiOiJ0ZXN0QWNjb3VudCIsImlhdCI6MTcxMDIyMTI1MCwiZXhwIjoxNzEwODI2MDUwfQ.wpMIUytr8MpqxGpFAJIlF8kG9OSm2KJE7xeUWQHVnAU")
    String token;

    @Schema(description = "회원 이름", example = "이원준")
    String memberName;

    @Schema(description = "회원 번호", example = "1")
    Long memberId;

    @Schema(description = "회원 닉네임", example = "test_nickname")
    String nickname;

    public KakaoLoginResponseDto(String token, String memberName, Long memberId, String nickname) {
        this.token = token;
        this.memberName = memberName;
        this.memberId = memberId;
        this.nickname = nickname;
    }
}
