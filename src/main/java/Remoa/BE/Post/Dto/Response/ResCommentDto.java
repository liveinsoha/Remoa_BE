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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResCommentDto {

    @Schema(description = "댓글 ID", example = "4")
    private Long commentId;

    @Schema(description = "작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "댓글 내용", example = "안녕")
    private String comment;

    @Schema(description = "좋아요 수", example = "0")
    private Integer likeCount;

    @Schema(description = "좋아요 여부")
    private Boolean isLiked;

    @Schema(description = "댓글 작성 시간", example = "2023-03-27T23:18:47")
    private LocalDateTime commentedTime;

    @Schema(description = "대댓글 목록")
    private List<ResReplyDto> replies;


}