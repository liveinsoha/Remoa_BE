package Remoa.BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public enum CustomMessage {
    //200 정상처리 상태코드
    OK(HttpStatus.OK,"올바른 요청","정상적으로 처리되었습니다"),
    OK_SIGNUP(HttpStatus.OK,"올바른 요청","회원가입하는 회원입니다"),

    //400 잘못된 요청
    VALIDATED(HttpStatus.BAD_REQUEST,"잘못된 요청","요청한 값이 유효성검사를 통과하지 못했습니다");

    private final HttpStatus httpStatus;
    private final String message;
    private final String detail;


    CustomMessage(HttpStatus httpStatus, String message, String detail) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.detail = detail;
    }

}
