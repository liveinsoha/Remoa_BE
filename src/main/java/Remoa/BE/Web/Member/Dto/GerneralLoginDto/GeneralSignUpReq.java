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
@Schema(description = "일반 회원가입 요청")
public class GeneralSignUpReq {

    @NotBlank
    @Schema(description = "이메일", example = "test1@gmail.com")
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", example = "testPassword1")
    private String password;

    @NotBlank
    @Schema(description = "이름", example = "김김김")
    private String name;

    public Member toEntity() {
        Member member = new Member();
        member.setEmail(this.email);
        member.setNickname("");
        member.setPassword(this.password);
        member.setName(this.name);
        member.setRole(Role.USER);
        return member;
    }
}
