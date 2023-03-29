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
            memberList = memberRepository.loadFollows(member); // member가 팔로우하는 유저 확인
        } else{ // 마이페이지 팔로워 관리 화면
            memberList = memberRepository.loadFollowers(member);
        }

        for(int i=0; i<memberList.size(); i++){
            Member followMember = memberList.get(i);
            // followMember가 팔로잉하는 유저 구하기
            List<Member> followingMemberFollowing = memberRepository.loadFollows(followMember);
            // followMember를 팔로우하는 유저 구하기(팔로워)
            List<Member> followingMemberFollower = memberRepository.loadFollowers(followMember);

            if(isFollowing == 1){ // 마이페이지 팔로잉 관리 화면
                ResMypageList resMypageList = ResMypageList.builder()
                        .profileImage(followMember.getProfileImage())
                        .userName(followMember.getNickname())
                        .followingNum(followingMemberFollowing.size())
                        .followerNum(followingMemberFollower.size())
                        .oneLineIntroduction(followMember.getOneLineIntroduction())
                        .build();
                resMypageLists.add(resMypageList);
            }else{ // 마이페이지 팔로워 관리 화면
                ResMypageList resMypageList = ResMypageList.builder()
                        .profileImage(followMember.getProfileImage())
                        .userName(followMember.getNickname())
                        .followingNum(followingMemberFollowing.size())
                        .followerNum(followingMemberFollower.size())
                        .oneLineIntroduction(followMember.getOneLineIntroduction())
                        .build();
                resMypageLists.add(resMypageList);
            }
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