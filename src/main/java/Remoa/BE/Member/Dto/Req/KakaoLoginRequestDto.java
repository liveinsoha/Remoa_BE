package Remoa.BE.Member.Dto.Req;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Domain.Role;
import Remoa.BE.Member.Dto.kakaoLoginDto.KakaoProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //컨트롤러에서 사용 안 됌.
public class KakaoLoginRequestDto {
    private String nickname;

    private String password;

    private String name;

    private String profileImageFileName;

    private Long kakaoIdentifier;

    public KakaoLoginRequestDto(KakaoProfile profile) {
        this.nickname = profile.getProperties().getNickname() + profile.getId(); //닉네임과 카카오 식별자를 가지고 임의로 account를 만듦.
        this.password = "password";
        this.name = profile.getProperties().getNickname();
        this.profileImageFileName = profile.getProperties().getProfile_image();
        this.kakaoIdentifier = profile.getId();
    }

    public Member toEntity() {
        Member member = new Member();
        member.setNickname(this.nickname);
        member.setPassword(this.password);
        member.setName(this.name);
        member.setKakaoId(this.kakaoIdentifier);
        member.setRole(Role.USER);
        return member;

    }

}
