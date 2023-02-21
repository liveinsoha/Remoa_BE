package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Form.LoginForm;
import Remoa.BE.Member.Service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static Remoa.BE.utill.MemberInfo.securityLoginWithoutLoginForm;

@Slf4j
@RestController
@RequiredArgsConstructor
@Deprecated
public class LoginController {

    private final LoginService loginService;

    /**
     * 이메일, 패스워드를 받아 일반 로그인을 하기 위해 사용.
     * 카카오 로그인으로 통합되어 현재는 사용되지 않음.
     * @param form
     * @param request
     * @return
     */

    @PostMapping("/login")
    public String login(@RequestBody LoginForm form, HttpServletRequest request) {

        log.info("login process activate");

        Member loginMember = loginService.login(form.getEmail(), form.getPassword());

        if (loginMember == null) {
            log.info("login process fail");
            return "login fail";
        }
        log.info("login process success");

        securityLoginWithoutLoginForm(loginMember);

        return loginMember.getNickname();
    }
}