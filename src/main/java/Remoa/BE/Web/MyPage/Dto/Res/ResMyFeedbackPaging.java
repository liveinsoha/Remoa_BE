package Remoa.BE.Web.MyPage.Dto.Res;

import Remoa.BE.Web.Post.Dto.Response.ResCommentFeedbackDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResMyFeedbackPaging {

    @Schema(description = "내가 작성한 최신 피드백들의 목록")
    private List<ResMyFeedbackDto> contents;

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "모든 피드백의 수")
    private long totalOfAllFeedbacks;

    @Schema(description = "현재 페이지의 피드백 수")
    private int totalOfPageElements;
}
