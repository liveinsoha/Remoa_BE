package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Form.KakaoSignupForm;
import Remoa.BE.Member.Service.KakaoService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.exception.response.FailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.*;
import static Remoa.BE.utill.MemberInfo.getMemberId;
import static Remoa.BE.utill.MemberInfo.securityLoginWithoutLoginForm;


@RestController
@Slf4j
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService ks;
    private final MemberService memberService;

    /**
     * 카카오 로그인을 통해 code를 query string으로 받아오면, 코드를 통해 토큰, 토큰을 통해 사용자 정보를 얻어와 db에 해당 사용자가 존재하는지 여부를
     * 파악해 존재할 때는 로그인, 없을 땐 회원가입 페이지로 넘어가게 해줌.
     */
    @GetMapping("/login/kakao")
    public ResponseEntity<Object> getCI(@RequestParam String code, HttpServletRequest request) throws IOException {
        log.info("code = " + code);

        // 액세스 토큰과 유저정보 받기
        String access_token = ks.getToken(code);
        Map<String, Object> userInfo = ks.getUserInfo(access_token);

        log.info("userInfo = {}", userInfo.values());

        Long kakaoId = Long.parseLong((String) userInfo.get("id"));
        Member kakaoMember = ks.distinguishKakaoId(kakaoId);

        log.info("kakaoId = {}", kakaoId);

        /*
         * 백에서 보낼 게 없을 때에도 정상 처리됐다는 메세지 정도는 같이 보내주는 게 좋습니다.
         * 상태메세지는 body에, 상태코드는 head에 들어갑니다.
         * 에러메세지를 exception 패키지처럼 한곳에 모아놓고 쓰는 것도 좋습니다.
         */
        if (kakaoMember == null) {
            //kakaoId가 db에 없으므로 kakaoMember가 null이므로 회원가입하지 않은 회원. 따라서 회원가입이 필요하므로 회원가입하는 uri로 redirect 시켜주어야 함.
            return successResponse(CustomMessage.OK, userInfo);
        } else {
            //if문에 걸리지 않았다면 이미 회원가입이 진행돼 db에 kakaoId가 있는 유저이므로 kakaoMember가 존재하므로 LoginController처럼 로그인 처리 하면 됩니다.
            return successResponse(CustomMessage.OK_SIGNUP, userInfo);
        }
    }

    /**
     * front-end에서 회원가입에 필요한 정보를 넘겨주면 KakaoSignupForm으로 받아 회원가입을 진행시켜줌
     */
    @PostMapping("/signup/kakao")
    public ResponseEntity<Object> signupKakaoMember(KakaoSignupForm form) {

        Member member = new Member();
        member.setKakaoId(form.getKakaoId());
        member.setEmail(form.getEmail());
        member.setNickname(form.getNickname());
        member.setProfileImage(form.getProfileImage());
        member.setTermConsent(form.getTermConsent());

        memberService.join(member);
        securityLoginWithoutLoginForm(member);
        return successResponse(CustomMessage.OK,member);
    }

    /**
     *자동 로그인입니다 프론트에서 jsession 쿠키를 건내주면 로그인 검증을 해줍니다
     */
    @GetMapping("/login")
    public ResponseEntity<Object> autoLogin(){
       Long memberId = getMemberId();
        if (memberId == null){
            return errorResponse(CustomMessage.UNAUTHORIZED);
        }
        else{
           Member member = memberService.findOne(memberId);
           return successResponse(CustomMessage.OK,member);
        }
    }



}