package Remoa.BE.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * 서비스 로직상 문제가 생길때 response
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    @Schema(description = "전달 메시지", example = "에러 메시지")
    private String message;

    @Schema(description = "디테일 설명", example = "이러이러한 이유로 안 됨")
    private String detail;

}
