package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
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

    private Long commentId;
    private ResMemberInfoDto member;
    private String comment;
    private Integer likeCount;
    private Boolean isLiked;
    private LocalDateTime commentedTime;
    private List<ResReplyDto> replies;

}