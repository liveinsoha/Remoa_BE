package Remoa.BE.Web.Member.Dto.GerneralLoginDto;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "일반 회원가입 요청")
public class GeneraSignUpReq {

    @NotBlank
    @Schema(description = "계정", example = "testNickname")
    private String nickname;

    @NotBlank
    @Schema(description = "비밀번호", example = "testPassword")
    private String password;

    @NotBlank
    @Schema(description = "이름", example = "이원준")
    private String name;

    public Member toEntity() {
        Member member = new Member();
        member.setNickname(this.nickname);
        member.setPassword(this.password);
        member.setName(this.name);
        member.setRole(Role.USER);
        return member;
    }
}
