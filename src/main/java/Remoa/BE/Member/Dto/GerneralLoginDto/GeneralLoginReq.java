package Remoa.BE.Member.Dto.GerneralLoginDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "일반 테스트 로그인 요청")
public class GeneralLoginReq {

    @Schema(description = "회원 계정", example = "testNickname")
    String nickname;

    @Schema(description = "회원 비밀번호", example = "testPassword")
    String password;
}
