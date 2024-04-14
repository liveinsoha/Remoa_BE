package Remoa.BE.Web.Member.Dto.Req;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Domain.Role;
import Remoa.BE.Web.Member.Dto.kakaoLoginDto.KakaoProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //컨트롤러에서 사용 안 됌.
public class KakaoLoginRequestDto {
    private String account;

    private String nickname;

    private String password;

    private String name;

    private String profileImageFileName;

    private Long kakaoIdentifier;

    private String email;

    public KakaoLoginRequestDto(KakaoProfile profile, String uniqueNickname) {
        this.account = profile.getProperties().getNickname() + profile.getId(); //닉네임과 카카오 식별자를 가지고 임의로 account를 만듦.
        this.nickname = uniqueNickname;
        this.password = "password";
        this.name = profile.getProperties().getNickname();
        this.profileImageFileName = profile.getProperties().getProfile_image();
        this.kakaoIdentifier = profile.getId();
        this.email = profile.getKakao_account().getEmail();
    }

    public Member toEntity() {
        Member member = new Member();
        member.setAccount(this.account);
        member.setEmail(this.email);
        member.setNickname(this.nickname);
        member.setPassword(this.password);
        member.setName(this.name);
        member.setKakaoId(this.kakaoIdentifier);
        member.setRole(Role.USER);
        return member;

    }

}
