package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Follow;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    //false 언팔 true 팔로우
    @Transactional
    public boolean followFunction(Long toMemberId, Member fromMember) {

        Member toMember = memberService.findOne(toMemberId);

        if (memberRepository.isFollow(fromMember, toMember)) {
            memberRepository.unfollowByFollowId(fromMember,toMember);
            return false;
        }

        Follow follow = new Follow();
        follow.setFromMember(fromMember);
        follow.setToMember(toMember);

        memberRepository.follow(follow);

        return true;
    }

    public List<Integer> followerAndFollowing(Member member){
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(memberRepository.loadFollowers(member).size());
        arr.add((member.getFollows().size()));
        return arr;
    }


    public List<Member> showFollows(Member fromMember) {
        return memberRepository.loadFollows(fromMember);
    }

    public List<Long> showFollowId(Member fromMember){
        return memberRepository.loadFollowsId(fromMember);
    }
}
