package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Domain.MemberProfile;
import Remoa.BE.Member.Service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // 프로필 수정 범위 : 닉네임(중복확인), 핸드폰번호, 대학교, 한줄소개
    @GetMapping("/user")
    public String editProfile(HttpServletRequest request) {

        HttpSession session = request.getSession();
        // 현재 로그인한 사용자의 세션 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 세션이 없으면 로그인 페이지로 이동
        if (loginMember == null) {
            return "redirect:/login/kakao";
        }
        return "profile edit page";
    }

    // RESTful API에서 PUT 매핑은 수정할 리소스를 명확하게 지정해야 하는데 이 경우에는 URL에 리소스 ID를 명시하는 것이 일반적이다.
    // 그런데 우리는 수정할 사용자의 정보를 모두 입력받아 수정하는 형태이기 때문에
    // URL에 리소스 ID를 명시할 필요가 없어서 PUT대신 POST 매핑을 사용하였습니다.
    @PostMapping("/user")
    public String editProfile(@RequestParam("nickname") String nickname,
                              @RequestParam("phoneNumber") String phoneNumber,
                              @RequestParam("university") String university,
                              @RequestParam("oneLineIntroduction") String oneLineIntroduction,
                              HttpServletRequest request) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            // 로그인되어 있지 않은 경우 로그인 페이지로 이동
            return "redirect:/login/kakao";
        }
        // 사용자의 입력 정보를 DTO에 담아 서비스로 전달
        MemberProfile profileInfo = new MemberProfile(nickname, phoneNumber, university, oneLineIntroduction);
        profileService.editProfile(loginMember.getKakaoId(), profileInfo);

        // 수정이 완료되면 프로필 페이지로 이동
        return "redirect:/user";
    }

}