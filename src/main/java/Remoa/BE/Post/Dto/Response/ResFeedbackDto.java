package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResFeedbackDto {

    @Schema(description = "피드백 ID", example = "123")
    private Long feedbackId;

    @Schema(description = "피드백 작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "피드백 내용", example = "좋은 정보 감사합니다.")
    private String feedback;

    @Schema(description = "페이지 번호", example = "1")
    private Integer page;

    @Schema(description = "좋아요 수", example = "10")
    private Integer likeCount;

    @Schema(description = "현재 사용자가 해당 피드백을 좋아하는지 여부", example = "true")
    private Boolean isLiked;

    @Schema(description = "피드백 작성 시간", example = "2024-04-05T08:30:00")
    private LocalDateTime feedbackTime;

    @Schema(description = "피드백에 대한 답글 목록")
    private List<ResReplyDto> replies;
}
