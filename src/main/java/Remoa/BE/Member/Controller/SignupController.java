package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Form.SignupForm;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.SignupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SignupController {

    private final SignupService memberService;

    @GetMapping("/signup")
    public String createForm() {
        return "회원가입 페이지 입니다.";
    }

    /**
     * front-end에서 회원가입을 위한 데이터를 넘겨 받아 회원가입 기능을 수행.
     * @param form
     * @return
     */
    @PostMapping("/signup")
    public String create(@RequestBody @Valid SignupForm form) {

        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        member.setBirth(form.getBirth());
        member.setSex(form.getSex());
        member.setPhoneNumber(form.getPhoneNumber());
        member.setTermConsent(form.getTermConsent());

        try{
            memberService.join(member);
        }
        catch (IllegalStateException e){
            return e.getMessage(); // 에러 메세지 프론트에 반환
        }

        return "signup success";
    }
}