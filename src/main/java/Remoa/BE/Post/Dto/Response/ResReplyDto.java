package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * comment, feedback의 대댓글에 모두 사용할 수 있음.
 */
@Data
@AllArgsConstructor
@Builder
public class ResReplyDto {

    private Long replyId;
    private ResMemberInfoDto member;
    private String content;
    private Integer likeCount;
    private Boolean isLiked;
    private LocalDateTime repliedTime;
}
