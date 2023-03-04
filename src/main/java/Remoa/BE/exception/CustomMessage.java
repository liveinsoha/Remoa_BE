package Remoa.BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public enum CustomMessage {
    //200 정상처리 상태코드
    OK(HttpStatus.OK,"올바른 요청","정상적으로 처리되었습니다"),

    OK_DUPLICATE(HttpStatus.OK,"올바른 요청","닉네임이 중복되었습니다"),

    OK_UN_DUPLICATE(HttpStatus.OK,"올바른 요청","사용가능한 닉네임 입니다"),

    OK_UNFOLLOW(HttpStatus.OK,"올바른 요청","회원을 언팔로잉 합니다"),

    //201 한 api에서 정상처리상 구분이 필요할떄 사용
    OK_FOLLOW(HttpStatus.CREATED,"올바른 요청","회원을 팔로잉 합니다"),

    OK_SIGNUP(HttpStatus.CREATED,"올바른 요청","회원가입하는 회원입니다"),

    //400 잘못된 요청
    VALIDATED(HttpStatus.BAD_REQUEST,"잘못된 요청","요청한 값이 유효성검사를 통과하지 못했습니다"),
    NO_MEMBER(HttpStatus.BAD_REQUEST,"잘못된 요청","요청한 memberId가 존재하지 않습니다"),

    FOLLOW_ME(HttpStatus.BAD_REQUEST,"서비스 로직상 오류","자신을 팔로우할 수 없습니다"),

    BAD_DUPLICATE(HttpStatus.BAD_REQUEST,"올바른 요청","닉네임이 중복되었습니다"),

    //401권한오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"권한이 없습니다","인증에 필요한 쿠키 정보가 없습니다");



    private final HttpStatus httpStatus;
    private final String message;
    private final String detail;


    CustomMessage(HttpStatus httpStatus, String message, String detail) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.detail = detail;
    }

}
