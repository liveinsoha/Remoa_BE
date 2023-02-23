package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.SignupService;
import Remoa.BE.Member.Service.WithdrewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WithdrewController {

    private final WithdrewService withdrewService;
    private final SignupService signupService;

    /**
     * PathVariable을 이용한 회원 탈퇴 uri.
     * 로그인 된 사용자인지, 해당 사용자의 탈퇴 요청이 맞는지 확인 후 탈퇴 처리.
     *
     * @param memberId
     * @param request
     * @return 로그인 되지 않은 상태면 403(forbidden), 다른 id 값을 통한 잘못된 요청을 하면 401(Unauthorized), 올바른 탈퇴 요청이면 200(OK)
     */
    @DeleteMapping("/delete/{member_id}")
    public ResponseEntity<String> withdrewRemoa(@PathVariable("member_id") Long memberId, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.FORBIDDEN);
        }

        //해당 객체로 탈퇴 수행해야 dirty checking 통한 soft delete 적용 가능.
        Member member = signupService.findOne(memberId);
        //PathVariable의 id와 로그인된 사용자의 id가 같은지 확인하기 위한 용도.
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember.getMemberId() != memberId) {
            return new ResponseEntity<>("잘못된 요청입니다.", HttpStatus.UNAUTHORIZED);
        }

        withdrewService.withdrewRemoa(member);

        return new ResponseEntity<>("회원 탈퇴가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * Session 정보만을 활용해서 PathVariable 없이 구현한 회원 탈퇴 uri. 로그인 된 사용자인지만 확인하면 됨.
     *
     * @param request
     * @return 로그인 되지 않은 상태면 403(forbidden), 올바른 탈퇴 요청이면 200(OK)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> withdrewRemoaWithoutPathVariable(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.FORBIDDEN);
        }

        Member member = (Member) session.getAttribute("loginMember");
        Member findMember = signupService.findOne(member.getMemberId());

        withdrewService.withdrewRemoa(findMember);

        return new ResponseEntity<>("회원 탈퇴가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * 탈퇴 처리가 되어 deleted 필드가 true인 member가 조회되는지 확인하기 위한 테스트 uri.
     *
     * @param memberId
     * @return 조회 결과가 없으면 418(I am a tea pot), 있으면 200(OK)
     */
    @GetMapping("/find/{member_id}")
    public ResponseEntity<String> findMemberTest(@PathVariable("member_id") Long memberId) {
        Member member = signupService.findOne(memberId);

        if (member == null) {
            return new ResponseEntity<>("회원 정보가 없습니다.", HttpStatus.I_AM_A_TEAPOT);
        }

        return new ResponseEntity<>("memberId 번호 <" + memberId + ">는 " + member.getName() + "입니다.", HttpStatus.OK);
    }

}
