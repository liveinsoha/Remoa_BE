package Remoa.BE.Web.Member.Dto.Res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResMypageFollowing {

    @Schema(description = "회원 ID", example = "123")
    private Long memberId;

    @Schema(description = "사용자 이름", example = "유저-980584")
    private String userName;

    @Schema(description = "팔로우 수", example = "3")
    private int followNum;

    @Schema(description = "팔로우 목록")
    private List<ResMypageList> resMypageList;

}
