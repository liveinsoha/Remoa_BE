package Remoa.BE.Member.Service;

import Remoa.BE.Member.Dto.Req.EditProfileForm;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import Remoa.BE.Member.Domain.Member;

import java.io.File;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final MemberService memberService;

    @Transactional
    public void editProfile(Long memberId, EditProfileForm profile) {
        Member member = memberService.findOne(memberId);

        if (member == null) {
            throw new IllegalArgumentException("Invalid member id");
        }

        // 사용자의 프로필 정보 수정
        member.setNickname(profile.getNickname());
        member.setPhoneNumber(profile.getPhoneNumber());
        member.setUniversity(profile.getUniversity());
        member.setOneLineIntroduction(profile.getOneLineIntroduction());
    }


}