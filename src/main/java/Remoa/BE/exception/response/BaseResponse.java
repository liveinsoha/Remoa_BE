package Remoa.BE.exception.response;

import Remoa.BE.exception.CustomMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BaseResponse<T> {

    @Schema(description = "전달 메시지")
    private String message;

    @Schema(description = "상세 설명")
    private String detail;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "전달 데이터")
    private T data;

    public BaseResponse(CustomMessage customMessage, T data){
        this.message = customMessage.getMessage();
        this.detail = customMessage.getDetail();
        this.data = data;
    }
    /**
     * @JsonInclude(JsonInclude.Include.NON_NULL) 어노테이션은 Jackson 라이브러리에서 사용되며,
     * JSON 직렬화 시에 data 필드가 null인 경우 JSON에서 생략되도록 설정합니다.
     * 이렇게 함으로써, 클라이언트에게 null 값이 포함된 데이터를 전달하지 않아도 됩니다.
     */

}