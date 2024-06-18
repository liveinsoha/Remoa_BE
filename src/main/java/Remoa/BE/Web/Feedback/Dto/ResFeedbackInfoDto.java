package Remoa.BE.Web.Feedback.Dto;

import Remoa.BE.Web.Feedback.Domain.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResFeedbackInfoDto {

    @Schema(description = "피드백 ID", example = "123")
    private Long feedbackId;

    @Schema(description = "피드백 내용", example = "좋은 정보 감사합니다.")
    private String feedback;

    @Schema(description = "페이지 번호", example = "1")
    private Integer page;

    @Schema(description = "좋아요 수", example = "10")
    private Integer likeCount;

    @Schema(description = "현재 사용자가 해당 피드백을 좋아하는지 여부", example = "true")
    private Boolean isLiked;

    @Schema(description = "삭제여부", example = "false")
    private Boolean isDeleted;

    @Schema(description = "피드백 작성 시간", example = "2024-04-05T08:30:00")
    private LocalDateTime feedbackTime;

    @Schema(description = "피드백에 대한 답글 목록")
    private List<ResFeedbackReplyDto> replies;

    public ResFeedbackInfoDto(Feedback feedback, Boolean isLiked, List<ResFeedbackReplyDto> replies) {
        this.feedbackId = feedback.getFeedbackId();
        this.feedback = feedback.getContent();
        this.page = feedback.getPageNumber();
        this.likeCount = feedback.getLikeCount();
        this.isLiked = isLiked;
        this.isDeleted = feedback.getDeleted();
        this.feedbackTime = feedback.getFeedbackTime();
        this.replies = replies;
    }
}
