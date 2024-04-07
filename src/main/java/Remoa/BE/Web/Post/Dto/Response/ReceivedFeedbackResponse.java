package Remoa.BE.Web.Post.Dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedFeedbackResponse {

    @Schema(description = "포스트의 댓글 목록")
    private List<ResReceivedCommentDto> posts;

    @Schema(description = "총 페이지 수")
    private int totalPages;

    @Schema(description = "총 댓글 수")
    private long totalOfAllComments;

    @Schema(description = "현재 페이지의 댓글 수")
    private int totalOfPageElements;

    // 생성자, 게터, 세터
}