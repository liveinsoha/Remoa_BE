package Remoa.BE.Web.Member.Dto.GerneralLoginDto;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Domain.Role;
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
@Schema(description = "어드민 회원가입 요청")
public class AdminSignUpReq {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    public Member toEntity() {
        Member member = new Member();
        member.setEmail(this.email);
        member.setNickname("유저");
        member.setPassword(this.password);
        member.setName(this.name);
        member.setRole(Role.ADMIN);
        return member;
    }
}