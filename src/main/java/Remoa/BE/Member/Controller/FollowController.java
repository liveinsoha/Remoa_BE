package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * Session에서 로그인한 사용자(Follow를 거는 사용자)의 Member와 Follow 받는 대상의 memberId를 통해
     * Follow가 이미 되었는지 확인하고, 안 되어 있으면 Follow, 되어있다면 Unfollow를 할 수 있도록 기능합니다.
     * @param memberId
     * @param request
     * @return ResponseEntity<String>
     */
    @PostMapping("/follow/{member_id}")
    public ResponseEntity<String> follow(@PathVariable("member_id") Long memberId, HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        //추후 Spring Security 설정을 통해 비로그인 사용자는 403 forbidden으로 자동으로 감출 수 있게 가능.
        if (session == null) {
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.FORBIDDEN);
        }
        Member fromMember = (Member) session.getAttribute("loginMember");
        if (fromMember.getMemberId() == memberId) {
            return new ResponseEntity<>("나 자신은 팔로우 할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        String followMessage = followService.followFunction(memberId, fromMember);

        return new ResponseEntity<>(followMessage, HttpStatus.OK);
    }

    /**
     * Login한 Member가 Follow하는 멤버들의 Member 필드들을 List로 보여줌.
     * 현재는 모든 필드들을 다 보여주지만, 이후 프론트와의 협의 후에 필요한 필드들만 가져올 수 있게 구현해야함.
     * @param request
     * @return ResponseEntity<Object>
     */
    @GetMapping("/user/follow")
    public ResponseEntity<Object> showFollowers(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        //추후 Spring Security 설정을 통해 비로그인 사용자는 403 forbidden으로 자동으로 감출 수 있게 가능.
        if (session == null) {
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.FORBIDDEN);
        }

        Member fromMember = (Member) session.getAttribute("loginMember");

        return new ResponseEntity<>(followService.showFollows(fromMember), HttpStatus.OK);
    }
}
