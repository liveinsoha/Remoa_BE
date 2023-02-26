package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.MemberProfile;
import Remoa.BE.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Remoa.BE.Member.Service.MemberService;


import Remoa.BE.Member.Domain.Member;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    @Autowired
    private MemberRepository memberRepository;
    private MemberService memberService;


    @Transactional
    public void editProfile(Long memberKakaoId, MemberProfile profile) {
        Member member = memberRepository.findByKakaoId(memberKakaoId).orElseThrow(() -> new IllegalArgumentException("Invalid member id"));

        // 닉네임이 중복되는지 검사
        if (!memberService.isNicknameDuplicate(member)){
            throw new IllegalArgumentException("Duplicate nickname");
        }

        // 사용자의 프로필 정보 수정
        member.setNickname(profile.getNickname());
        member.setPhoneNumber(profile.getPhoneNumber());
        member.setUniversity(profile.getUniversity());
        member.setOneLineIntroduction(profile.getOneLineIntroduction());
    }
}