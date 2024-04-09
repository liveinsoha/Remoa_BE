package Remoa.BE.Web.Comment.Dto.Res;

import Remoa.BE.Web.Comment.Domain.CommentReply;
import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResCommentReplyDto {

    @Schema(description = "대댓글 ID", example = "4")
    private Long commentReplyId;

    @Schema(description = "작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "대댓글 내용", example = "안녕")
    private String content;

    @Schema(description = "좋아요 수", example = "0")
    private Integer likeCount;

    @Schema(description = "좋아요 여부") // 수정 필요
    private Boolean isLiked;

    @Schema(description = "대댓글 작성 시간", example = "2023-03-27T23:18:47")
    private LocalDateTime commentRepliedTime;

    @Schema(description = "삭제여부", example = "false")
    private Boolean isDeleted;

    public ResCommentReplyDto(CommentReply commentReply, Boolean isLiked, Boolean isFollow) {
        this.commentReplyId = commentReply.getCommentReplyId();
        this.member = new ResMemberInfoDto(commentReply.getMember(), isFollow);
        this.content = commentReply.getContent();
        this.likeCount = commentReply.getLikeCount();
        this.isLiked = isLiked;
        this.isDeleted = commentReply.getDeleted();
        this.commentRepliedTime = commentReply.getCommentRepliedTime();
    }

}
