package Remoa.BE.Post.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResReplyDto {

    private Long commentId;
    private Long memberId;
    private String nickname;
    private String profileImage;
    private String comment;
    private Integer likeCount;
}
