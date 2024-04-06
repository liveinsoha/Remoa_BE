package Remoa.BE.Member.Dto.GerneralLoginDto;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "일반 회원가입 요청")
public class AdminSignUpReq {

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    public Member toEntity() {
        Member member = new Member();
        member.setNickname(this.nickname);
        member.setPassword(this.password);
        member.setName(this.name);
        member.setRole(Role.ADMIN);
        return member;
    }
}