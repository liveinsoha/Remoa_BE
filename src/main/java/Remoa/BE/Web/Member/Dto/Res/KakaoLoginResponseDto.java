package Remoa.BE.Web.Member.Dto.Res;

import Remoa.BE.Web.Member.Domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class KakaoLoginResponseDto {

    @Schema(description = "JWT 인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QWNjb3VudCIsImFjY291bnQiOiJ0ZXN0QWNjb3VudCIsImlhdCI6MTcxMDIyMTI1MCwiZXhwIjoxNzEwODI2MDUwfQ.wpMIUytr8MpqxGpFAJIlF8kG9OSm2KJE7xeUWQHVnAU")
    String token;


    @Schema(description = "회원 닉네임", example = "test_nickname")
    String nickname;

    @Schema(description = "회원 이름", example = "이원준")
    String name;

    @Schema(description = "회원 번호", example = "1")
    Long memberId;

    @Schema(description = "회원 역할", example = "USER")
    String role;


    public KakaoLoginResponseDto(String token, Member member) {
        this.token = token;
        this.nickname = member.getNickname();
        this.name = member.getName();
        this.memberId = member.getMemberId();
        this.role = member.getRole().toString();
    }
}
