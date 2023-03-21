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

    private final MemberRepository memberRepository;

    public List<ResMypageList> findResMypageList(Member member, int isFollowing){
        List<ResMypageList> resMypageLists = new ArrayList<>();
        List<Member> memberList;

        if(isFollowing == 1) { // 마이페이지 팔로잉 관리 화면
            memberList = memberRepository.loadFollows(member);
        } else{ // 마이페이지 팔로워 관리 화면
            memberList = memberRepository.loadFollowers(member);
        }

        for(int i=0; i<memberList.size(); i++){
            ResMypageList resMypageList = ResMypageList.builder()
                    .profileImage(memberList.get(i).getProfileImage())
                    .userName(memberList.get(i).getNickname())
                    .followingNum(memberList.size())
                    .followerNum(memberRepository.loadFollowers(memberList.get(i)).size())
                    .build();
            resMypageLists.add(resMypageList);
        }
        return resMypageLists;
    }

    /**
     * 마이페이지 팔로잉 관리 화면에 사용
     * @param member
     * @return ResMypageFollowing
     */
    @Transactional
    public ResMypageFollowing mypageFollowing(Member member){

        return ResMypageFollowing.builder()
                .userName(member.getNickname())
                .followNum(member.getFollows().size()) // 내가 팔로우하고 있는 유저 수
                .resMypageList(findResMypageList(member, 1))
                .build();
    }

    /**
     * 마이페이지 팔로워 관리 화면에 사용
     * @param member
     * @return ResMypageFollowing
     */
    @Transactional
    public ResMypageFollowing mypageFollower(Member member){

        return ResMypageFollowing.builder()
                .userName(member.getNickname())
                .followNum(memberRepository.loadFollowers(member).size()) // 나를 팔로우하고 있는 유저 수
                .resMypageList(findResMypageList(member, 0))
                .build();
    }
}
