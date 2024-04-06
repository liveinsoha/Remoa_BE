package Remoa.BE.config.jwt;

import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /*
    이 곳은 인증이 필요한 api에 접근 시 인증에 실패할 경우 이곳으로 진입하여 Response객체를 정의한다.
     토큰이 없거나, 만료되었거나, 유효하지 않거나 셋 중에 하나이다.
     @ControllerAdvice에서 에러를 처리하는 건 서블릿 대상이다.
     필터의 에러는 이 곳에서 처리할 수 있다. 필터에서 에러가 발생한 경우
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute(JwtProperties.HEADER_STRING);
        String errorCode;

        if (exception == null) { //토큰이 없고, 인증 못해서 에러 발생한 경우
            setErrorResponse(response, CustomMessage.NO_TOKEN_FOUND);
            return;
        }

        if(exception.equals("토큰이 만료되었습니다.")) {
            setErrorResponse(response, CustomMessage.TOKEN_EXPIRED);
            return;
        }

        if (exception.equals("유효하지 않은 토큰입니다.")) { //토큰이 있지만, 로그인을(인증) 하지 못한 경우
            setErrorResponse(response, CustomMessage.NOT_VALID_TOKEN);
            return;
        }
    }

    private void setErrorResponse( // 여기서 에러의 경우 반환 데이터를 정의
            HttpServletResponse response,
                                   CustomMessage customMessage
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(customMessage.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail(customMessage.getMessage())
                .message(customMessage.getDetail())
                .build();

        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setResponse(HttpServletResponse response, String errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JwtProperties.HEADER_STRING + " : " + errorCode);
    }
}
