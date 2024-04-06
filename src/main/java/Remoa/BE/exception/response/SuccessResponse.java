package Remoa.BE.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 성공적인 처리를 할떄 response
 * post,put일때는 data 에다가 반영된 값을 넣어준다
 * ex)회원가입을 성공하면 member에대한 정보를 넣어줌
 */
@Getter
@Setter
@Builder
public class SuccessResponse{

    @Schema(description = "전달 메시지", example = "올바른 요청")
    private String message;

    @Schema(description = "상세 설명", example = "이러이러한 이유입니다")
    private String detail;

    @Schema(description = "전달 데이터")
    private Object data;

}
