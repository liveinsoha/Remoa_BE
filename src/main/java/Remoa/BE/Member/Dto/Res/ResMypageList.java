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
}
