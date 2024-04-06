package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Follow;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Repository.FollowRepository;
import Remoa.BE.Member.Repository.MemberRepository;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
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

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    //false 언팔 true 팔로우
    @Transactional
    public boolean followFunction(Long fromMemberId, Long toMemberId) {

        Member fromMember = memberRepository.getReferenceById(fromMemberId);
        Member toMember = memberRepository.findById(toMemberId).orElseThrow(() -> new BaseException(CustomMessage.NO_ID));

        System.out.println("fromMember = " + fromMember);
        System.out.println("toMember = " + toMember);

        if (followRepository.isFollow(fromMember, toMember)) {
            followRepository.unfollowByMembers(fromMember,toMember);
            return false;
        }

        Follow follow = new Follow();
        follow.setFromMember(fromMember);
        follow.setToMember(toMember);

        //memberRepository.follow(follow);
        followRepository.save(follow);

        return true;
    }

    public Boolean isMyMemberFollowMember(Member myMember, Member member) {
        //return memberRepository.isFollow(myMember, member);
        return followRepository.isFollow(myMember, member);
    }

    public List<Integer> followerAndFollowing(Member member){
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(followRepository.loadFollowers(member).size());
        arr.add((member.getFollows().size()));
        return arr;
    }


    public List<Member> showFollows(Member fromMember) {
        return followRepository.loadFollows(fromMember);
    }

    public List<Long> showFollowId(Member fromMember){
        return followRepository.loadFollowsId(fromMember);
    }
}
