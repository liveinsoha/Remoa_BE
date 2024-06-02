package Remoa.BE.Web.Feedback.Dto;

import Remoa.BE.Web.Feedback.Domain.Feedback;
import Remoa.BE.Web.Feedback.Domain.FeedbackReply;
import Remoa.BE.Web.Feedback.Dto.ResFeedbackReplyDto;
import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Schema(description = "삭제여부", example = "false")
    private Boolean isDeleted;


    @Schema(description = "피드백 작성 시간", example = "2024-04-05T08:30:00")
    private LocalDateTime feedbackTime;

    @Schema(description = "피드백에 대한 답글 목록")
    private List<ResFeedbackReplyDto> replies;


    public ResFeedbackDto(Feedback feedback, Boolean isLiked, Boolean isFollow, List<ResFeedbackReplyDto> feedbackReplyDtos) {
        this.feedbackId = feedback.getFeedbackId();
        this.member = new ResMemberInfoDto(feedback.getMember(), isFollow);
        this.feedback = feedback.getContent();
        this.page = feedback.getPageNumber();
        this.likeCount = feedback.getLikeCount();
        this.feedbackTime = feedback.getFeedbackTime();
        this.isLiked = isLiked;
        this.isDeleted = feedback.getDeleted();
        this.replies = feedbackReplyDtos;
    }
}
