package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Follow;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final MemberRepository memberRepository;

    @Transactional
    public String followFunction(Long toMemberId, Member fromMember) {

        Member toMember = memberRepository.findOne(toMemberId);

        if (memberRepository.isFollow(fromMember, toMember)) {
            Follow follow = memberRepository.loadFollow(fromMember, toMember);
            memberRepository.unfollowByFollowId(follow.getFollowId());
            return fromMember.getMemberId() + " unfollowed " + toMember.getMemberId();
        }

        Follow follow = Follow.followSomeone(toMember, fromMember);

        memberRepository.follow(follow);

        return fromMember.getMemberId() + " followed " + toMember.getMemberId();
    }

    public List<Member> showFollows(Member fromMember) {
        return memberRepository.loadFollows(fromMember);
    }
}
