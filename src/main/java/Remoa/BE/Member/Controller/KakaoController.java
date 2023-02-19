package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Form.KakaoSignupForm;
import Remoa.BE.Member.Service.KakaoService;
import Remoa.BE.Member.Service.SignupService;
import Remoa.BE.exception.CustomBody;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService ks;
    private final SignupService signupService;

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
            securityLoginWithoutLoginForm(request, kakaoMember);
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

        signupService.join(member);
        return successResponse(CustomMessage.OK,member);
    }

    /**
     * Spring Security가 기본값으로 form data를 사용해 로그인을 진행하는데, Rest API를 이용해 json을 주고받는 방식으로 로그인을 처리하기 위해
     * 우회적인 방식으로 Spring Security를 이용할 수 있게 해주는 메서드.
     */
    private void securityLoginWithoutLoginForm(HttpServletRequest request, Member member) {

        //로그인 세션에 들어갈 권한을 설정합니다.
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));

        SecurityContext sc = SecurityContextHolder.getContext();
        //아이디, 패스워드, 권한을 설정합니다. 아이디는 Object단위로 넣어도 무방하며
        //패스워드는 null로 하여도 값이 생성됩니다.
        sc.setAuthentication(new UsernamePasswordAuthenticationToken(member, null, list));
        HttpSession session = request.getSession(true);
        session.setAttribute("loginMember", member);

        //위에서 설정한 값을 Spring security에서 사용할 수 있도록 세션에 설정해줍니다.
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
    }
}