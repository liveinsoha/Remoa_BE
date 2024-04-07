package Remoa.BE.Web.Post.Dto.Response;

import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "대댓글 ID", example = "3")
    private Long replyId;

    @Schema(description = "작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "대댓글 내용", example = "대박")
    private String content;

    @Schema(description = "좋아요 수", example = "0")
    private Integer likeCount;

    @Schema(description = "좋아요 여부")
    private Boolean isLiked;

    @Schema(description = "대댓글 작성 시간", example = "2023-03-27T23:18:38")
    private LocalDateTime repliedTime;
}
