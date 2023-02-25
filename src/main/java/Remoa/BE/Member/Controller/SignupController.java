package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Role;
import Remoa.BE.Member.Form.SignupForm;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Deprecated
public class SignupController {

    private final MemberService memberService;


    /**
     * front-end에서 회원가입을 위한 데이터를 넘겨 받아 회원가입 기능을 수행.
     * 카카오 로그인으로 통합되어 현재는 사용되지 않음.
     * @param form
     * @return
     */
   /* @Deprecated
    @PostMapping("/signup")
    public String create(@RequestBody SignupForm form) {

        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        member.setBirth(form.getBirth());
        member.setSex(form.getSex());
        member.setPhoneNumber(form.getPhoneNumber());
        member.setTermConsent(form.getTermConsent());
        member.setRole(Role.USER.getValue());

        try{
            memberService.join(member);
        }
        catch (IllegalStateException e){
            return e.getMessage(); // 에러 메세지 프론트에 반환
        }

        return "signup success";
    }*/
}