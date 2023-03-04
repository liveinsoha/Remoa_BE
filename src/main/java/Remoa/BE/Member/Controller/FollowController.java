package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FollowController {

    private final FollowService followService;
    private final MemberService memberService;

    /**
     * Session에서 로그인한 사용자(Follow를 거는 사용자)의 Member와 Follow 받는 대상의 memberId를 통해
     * Follow가 이미 되었는지 확인하고, 안 되어 있으면 Follow, 되어있다면 Unfollow를 할 수 있도록 기능합니다.
     * @param memberId
     * @param request
     * @return ResponseEntity<String>
     */
    @PostMapping("/follow/{member_id}")
    public ResponseEntity<Object> follow(@PathVariable("member_id") Long memberId, HttpServletRequest request) {

        if(authorized(request)){
            //나 자신을 팔로우 하는 경우
            Long myMemberId = getMemberId();
            if(Objects.equals(memberId, myMemberId)){

                return errorResponse(CustomMessage.FOLLOW_ME);
            }
            else{
                Member member = memberService.findOne(myMemberId);
                boolean check = followService.followFunction(memberId, member);
                //팔로우
                if(check){
                    return successResponse(CustomMessage.OK_FOLLOW,followService.showFollowId(member));
                }
                //언팔로우
                else{
                    return successResponse(CustomMessage.OK_UNFOLLOW,followService.showFollowId(member));
                }

            }

        }

        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    /**
     * Login한 Member가 Follow하는 멤버들의 Member 필드들을 List로 보여줌.
     * 현재는 모든 필드들을 다 보여주지만, 이후 프론트와의 협의 후에 필요한 필드들만 가져올 수 있게 구현해야함.
     * @param request
     * @return ResponseEntity<Object>
     */
    @GetMapping("/user/follow")
    public ResponseEntity<Object> showFollowers(HttpServletRequest request) {
        if(authorized(request)){
            Long myMemberId = getMemberId();
            Member member = memberService.findOne(myMemberId);
            List<Member> members = followService.showFollows(member);
            return successResponse(CustomMessage.OK,members);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }
}
