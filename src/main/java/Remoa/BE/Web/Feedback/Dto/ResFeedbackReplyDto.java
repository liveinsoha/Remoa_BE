package Remoa.BE.Web.Feedback.Dto;

import Remoa.BE.Web.Feedback.Domain.FeedbackReply;
import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResFeedbackReplyDto {

    @Schema(description = "피드백 답글 ID", example = "4")
    private Long feedbackReplyId;

    @Schema(description = "작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "피드백 답글 내용", example = "안녕하세요")
    private String content;

    @Schema(description = "좋아요 수", example = "0")
    private Integer likeCount;

    @Schema(description = "좋아요 여부")
    private Boolean isLiked;

    @Schema(description = "피드백 답글 작성 시간", example = "2023-03-27T23:18:47")
    private LocalDateTime feedbackReplyTime;

    public ResFeedbackReplyDto(FeedbackReply feedbackReply, Boolean isLiked, Boolean isFollow) {
        this.feedbackReplyId = feedbackReply.getFeedbackReplyId();
        this.member = new ResMemberInfoDto(feedbackReply.getMember(), isFollow);
        this.content = feedbackReply.getContent();
        this.likeCount = feedbackReply.getLikeCount();
        this.isLiked = isLiked;
        this.feedbackReplyTime = feedbackReply.getFeedbackReplyTime();
    }
}
