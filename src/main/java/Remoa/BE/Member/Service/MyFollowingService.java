package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMypageFollowing;
import Remoa.BE.Member.Dto.Res.ResMypageList;
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
public class MyFollowingService {

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    public List<ResMypageList> findResMypageList(Member member){
        List<ResMypageList> resMypageLists = new ArrayList<>();
        List<Member> memberList = memberRepository.loadFollows(member);
        List<Member> followerList = memberRepository.loadFollowers(member);

        // 내가 팔로우하는 사람 수 -> memberList의 개수
        // 나를 팔로우하는 사람 수 어떻게 구해야하나

        for(int i=0; i<memberList.size(); i++){
            ResMypageList resMypageList = ResMypageList.builder()
                    .profileImage(memberList.get(i).getProfileImage())
                    .userName(memberList.get(i).getName())
                    .followingNum(memberList.size())
                    .followerNum(followerList.size())
                    .build();
            resMypageLists.add(resMypageList);
        }
        return resMypageLists;
    }

    @Transactional
    public ResMypageFollowing mypageFollowing(Member member){

        return ResMypageFollowing.builder()
                .userName(member.getNickname())
                .followNum(member.getFollows().size())
                .resMypageList(findResMypageList(member))
                .build();
    }

//    @Transactional
//    public ResMypageFollowing mypageFollower(Member member){
//
//    }
}
