package Remoa.BE.Web.Member.Dto.Res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResMypageList {

    @Schema(description = "프로필 이미지 URL", example = "https://remoa.s3.ap-northeast-2.amazonaws.com/img/profile_img.png")
    private String profileImage;

    @Schema(description = "사용자 이름", example = "유저-741885")
    private String userName;

    @Schema(description = "팔로잉 수", example = "0")
    private int followingNum;

    @Schema(description = "팔로워 수", example = "1")
    private int followerNum;

    @Schema(description = "한 줄 소개", example = "")
    private String oneLineIntroduction;

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "팔로잉 여부", example = "true")
    private Boolean isFollow; // 팔로워 목록에서 내가 팔로워를 팔로잉하는지 확인하기 위함. 팔로잉 목록에선 필요 없으므로 null
}
