package Remoa.BE.Web.Member.Dto.Res;

import Remoa.BE.Web.Member.Domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResMemberInfoDto {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "닉네임", example = "유저-250900")
    private String nickname;

    @Schema(description = "프로필 이미지 URL")
    private String profileImage;

    @Schema(description = "팔로우 여부")
    private Boolean isFollow;

    public ResMemberInfoDto(Member member, boolean isFollow) {
        this.memberId = member.getMemberId();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.isFollow = isFollow;
    }
}
