package Remoa.BE.Member.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResFollowerAndFollowingDto;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@RestController
@Slf4j
@RequiredArgsConstructor
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

        if (authorized(request)) {
            //나 자신을 팔로우 하는 경우
            Long myMemberId = getMemberId();
            if (Objects.equals(memberId, myMemberId)) {

                return errorResponse(CustomMessage.FOLLOW_ME);
            } else {
                Member member = memberService.findOne(myMemberId);
                boolean check = followService.followFunction(memberId, member);
                //팔로우
                if (check) {
                    return new ResponseEntity<>(HttpStatus.CREATED);
                }
                //언팔로우
                else {
                    return new ResponseEntity<>(HttpStatus.OK);

                }

            }
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    /**
     * @return ResponseEntity<Object>
     */
    @GetMapping("/follow/{member_id}")
    public ResponseEntity<Object> showFollowers(@PathVariable("member_id") Long memberId) {
        Member member = memberService.findOne(memberId);
        List<Integer> count = followService.followerAndFollowing(member);
        ResFollowerAndFollowingDto result = ResFollowerAndFollowingDto.builder()
                .follower(count.get(0))
                .following(count.get(1))
                .build();
        return successResponse(CustomMessage.OK,result);
    }
}
