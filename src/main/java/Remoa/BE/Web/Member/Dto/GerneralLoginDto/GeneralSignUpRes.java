package Remoa.BE.Web.Member.Dto.GerneralLoginDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "일반 회원가입 응답")
public class GeneralSignUpRes {

    @Schema(description = "회원번호", example = "1")
    Long memberId;

    public GeneralSignUpRes(Long memberId) {
        this.memberId = memberId;
    }
}
