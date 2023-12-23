package Remoa.BE.Member.Dto.Res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResMypageList {
    private String profileImage;

    private String userName;

    private int followingNum;

    private int followerNum;

    private String oneLineIntroduction;

    private Long memberId;

    // 팔로워 목록에서 내가 팔로워를 팔로잉하는지 확인하기 위함. 팔로잉 목록에선 필요 없으므로 null
    private Boolean isFollow;
}
