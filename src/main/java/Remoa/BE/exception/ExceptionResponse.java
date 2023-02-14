package Remoa.BE.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Exception 발생시 해당 Exception에 관한 세부사항을 정리해주는 폼
 */
@Data
@AllArgsConstructor
public class ExceptionResponse {
    private String code; //에러메세지 표시
    private String description; //간략 설명
    private String detail; //DTO에서 설정한 에러 메세지 값
    private LocalDateTime timestamp; //에러 발생 시간
}
