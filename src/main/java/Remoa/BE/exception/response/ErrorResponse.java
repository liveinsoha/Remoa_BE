package Remoa.BE.exception.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * 서비스 로직상 문제가 생길때 response
 */
@Builder
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private String detail;

}
