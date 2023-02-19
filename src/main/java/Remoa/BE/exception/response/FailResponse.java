package Remoa.BE.exception.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 유효성 검사상 문제가 생길때 response
 */
@Builder
@Getter
@Setter
public class FailResponse {
    private String message;
    private String detail;
    private Object cause;
}
