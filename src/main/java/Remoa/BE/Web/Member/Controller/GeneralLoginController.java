package Remoa.BE.Web.Member.Controller;

import Remoa.BE.Web.Member.Dto.GerneralLoginDto.GeneralLoginReq;
import Remoa.BE.Web.Member.Dto.GerneralLoginDto.GeneralLoginRes;
import Remoa.BE.Web.Member.Dto.GerneralLoginDto.GeneralSignUpReq;
import Remoa.BE.Web.Member.Dto.GerneralLoginDto.GeneralSignUpRes;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseResponse;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;


@Tag(name = "일반 로그인 Test Completed", description = "실제 사용하지 않지만 테스트용 토큰 얻기 위한 가입 및 로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
public class GeneralLoginController {

    private final MemberService memberService;
    private final Random random = new Random();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageUtils.SUCCESS),
            @ApiResponse(responseCode = "400", description = MessageUtils.BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "테스트용 일반 회원가입 Test completed", description = "email, password 기반 일반 회원가입입니다. <br>리턴 데이터는 회원번호입니다")
    @PostMapping("/signUp")
    public ResponseEntity<BaseResponse<GeneralSignUpRes>> signUp(@Parameter(name = "회원가입 위한 회원 정보들", required = true) @Valid @RequestBody GeneralSignUpReq signUpReq) {

        BaseResponse<GeneralSignUpRes> response = new BaseResponse<>(CustomMessage.OK, memberService.generalSignUp(signUpReq));

        return ResponseEntity.ok(response);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageUtils.SUCCESS),
            @ApiResponse(responseCode = "404", description = MessageUtils.NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "테스트용 일반 로그인 Test completed", description = "nickname, password 기반 일반 로그인입니다. ")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<GeneralLoginRes>> login(@Parameter(name = "로그인 위한 회원 정보들", required = true) @RequestBody GeneralLoginReq loginRequestDto) {
        BaseResponse<GeneralLoginRes> response = new BaseResponse<>(CustomMessage.OK, memberService.generalLogin(loginRequestDto));
        return ResponseEntity.ok(response);
    }
}
