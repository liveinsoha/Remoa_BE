package Remoa.BE.Member.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfile {

    // 프로필 수정 범위 : 닉네임(중복확인), 핸드폰번호, 대학교(대학교 리스트 검색하기), 한줄소개
    private String nickname;
    private String phoneNumber;
    private String university;
    private String oneLineIntroduction;
}
