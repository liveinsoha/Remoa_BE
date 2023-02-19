package Remoa.BE.exception;

import Remoa.BE.exception.response.FailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.*;


@Slf4j
@RestControllerAdvice //모든 컨트롤러가 호출되기 전에 사전 실행됨. -> 이를 통해 모든 예외가 처리되는 클래스가 만들어졌다.
public class CustomizedExceptionHandler {

    /**
     유효성 검사 에러 처리
     */
    @ExceptionHandler
    public ResponseEntity<Object> methodValidException(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return failResponse(CustomMessage.VALIDATED,errors);
    }
}
