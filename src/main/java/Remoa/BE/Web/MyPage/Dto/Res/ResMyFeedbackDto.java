package Remoa.BE.Web.MyPage.Dto.Res;

import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResMyFeedbackDto {

    @Schema(description = "게시물 제목", example = "서울 빅데이터 공모전 최우수상")
    private String title;

    @Schema(description = "게시물 ID", example = "2")
    private Long postId;

    @Schema(description = "피드백 ID")
    private Long feedbackId;

    @Schema(description = "썸네일 URL", example = "https://remoa.s3.ap-northeast-2.amazonaws.com/thumbnail/3d6655e8-d922-4da4-aeac-5edc2a5cc0ca_basic_profile.png")
    private String thumbnail;

    @Schema(description = "게시물 작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "피드백 내용", example = "잘했어요")
    private String content;

    @Schema(description = "좋아요 수", example = "0")
    private Integer likeCount;
}
