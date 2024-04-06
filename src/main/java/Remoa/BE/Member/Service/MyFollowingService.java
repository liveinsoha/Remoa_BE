package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMypageFollowing;
import Remoa.BE.Member.Dto.Res.ResMypageList;
import Remoa.BE.Member.Repository.FollowRepository;
import Remoa.BE.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyFollowingService {

    private final FollowRepository followRepository;

    private final FollowService followService;

    public List<ResMypageList> findResMypageList(Member member, int isFollowing){
        List<ResMypageList> resMypageLists = new ArrayList<>();
        List<Member> memberList;

        if(isFollowing == 1) { // 마이페이지 팔로잉 관리 화면
            memberList = followRepository.loadFollows(member); // member가 팔로우하는 유저 확인
            log.warn("followList : ");
            memberList.forEach(m -> log.warn(m.getNickname()));
        } else{ // 마이페이지 팔로워 관리 화면
            memberList = followRepository.loadFollowers(member);
            log.warn("followerList : ");
            memberList.forEach(m -> log.warn(m.getNickname()));
        }

        for (Member followMember : memberList) {
            // followMember가 팔로잉하는 유저 구하기
            List<Member> followingMemberFollowing = followRepository.loadFollows(followMember);
            // followMember를 팔로우하는 유저 구하기(팔로워)
            List<Member> followingMemberFollower = followRepository.loadFollowers(followMember);
            if (isFollowing == 1) { // 마이페이지 팔로잉 관리 화면
                ResMypageList resMypageList = ResMypageList.builder()
                        .profileImage(followMember.getProfileImage())
                        .userName(followMember.getNickname())
                        .followingNum(followingMemberFollowing.size())
                        .followerNum(followingMemberFollower.size())
                        .oneLineIntroduction(followMember.getOneLineIntroduction())
                        .memberId(followMember.getMemberId())
                        // 팔로잉 목록에서는 팔로워를 팔로잉하는지 확인할 필요가 없으므로 null처리
                        .isFollow(null)
                        .build();
                resMypageLists.add(resMypageList);
            } else { // 마이페이지 팔로워 관리 화면
                ResMypageList resMypageList = ResMypageList.builder()
                        .profileImage(followMember.getProfileImage())
                        .userName(followMember.getNickname())
                        .followingNum(followingMemberFollowing.size())
                        .followerNum(followingMemberFollower.size())
                        .oneLineIntroduction(followMember.getOneLineIntroduction())
                        .memberId(followMember.getMemberId())
                        // 팔로워를 팔로잉하는지 확인 - ture : 팔로우 함 / false : 팔로우 안 함
                        .isFollow(followService.isMyMemberFollowMember(member, followMember))
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
                .memberId(member.getMemberId())
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
                .memberId(member.getMemberId())
                .userName(member.getNickname())
                .followNum(followRepository.loadFollowers(member).size()) // 나를 팔로우하고 있는 유저 수
                .resMypageList(findResMypageList(member, 0))
                .build();
    }


}