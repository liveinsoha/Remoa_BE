package Remoa.BE.Post.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResCommentDto {

    private Long commentId;
    private Long memberId;
    private String nickname;
    private String profileImage;
    private String comment;
    private Integer likeCount;
    private List<ResReplyDto> replies;

}