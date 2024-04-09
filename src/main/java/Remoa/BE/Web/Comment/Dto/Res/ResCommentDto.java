package Remoa.BE.Web.Comment.Dto.Res;

import Remoa.BE.Web.Comment.Domain.CommentReply;
import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResCommentDto {

    @Schema(description = "코멘트 ID", example = "4")
    private Long commentId;

    @Schema(description = "작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "코멘트 내용", example = "안녕")
    private String content;

    @Schema(description = "좋아요 수", example = "0")
    private Integer likeCount;

    @Schema(description = "좋아요 여부")
    private Boolean isLiked;

    @Schema(description = "삭제여부", example = "false")
    private Boolean isDeleted;

    @Schema(description = "코멘트 작성 시간", example = "2023-03-27T23:18:47")
    private LocalDateTime commentedTime;

    @Schema(description = "대댓글 목록")
    private List<ResCommentReplyDto> commentReplies;

    public ResCommentDto(Comment comment, Boolean isLiked, Boolean isFollow, List<ResCommentReplyDto> commentReplyDtos) {
        this.commentId = comment.getCommentId();
        this.member = new ResMemberInfoDto(comment.getMember(), isFollow);
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.isLiked = isLiked;
        this.isDeleted = comment.getDeleted();
        this.commentedTime = comment.getCommentedTime();
        this.commentReplies = commentReplyDtos;
    }


}