package Remoa.BE.Web.Post.Dto.Response;

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
public class ResMyActivityDto {

    @Schema(description = "최신 댓글 또는 피드백 정보")
    private ResCommentFeedbackDto content;

    @Schema(description = "최근에 스크랩한 게시물 리스트")
    private List<ResPostDto> posts;
}