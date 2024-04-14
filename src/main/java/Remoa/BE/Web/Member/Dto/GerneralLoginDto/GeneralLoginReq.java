package Remoa.BE.Web.Member.Dto.GerneralLoginDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "일반 테스트 로그인 요청")
public class GeneralLoginReq {

    @Schema(description = "계정", example = "test1@gmail.com")
    String account;

    @Schema(description = "회원 비밀번호", example = "testPassword1")
    String password;
}
