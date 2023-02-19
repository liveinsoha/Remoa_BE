package Remoa.BE.exception.response;

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
public class SuccessResponse {
    private String message;
    private String detail;
    private Object data;

}
