package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import lombok.Builder;
import lombok.Data;

/**
 * 내 활동 관리에 쓰이는 Comment와 Feedback을 구분 없이 최신순으로 볼러오는 데 쓰이는 dto.
 */
@Data
@Builder
public class ResCommentFeedbackDto {

    private String title;
    private Long postId;
    private String thumbnail;
    private ResMemberInfoDto member;
    private String content;
    private Integer likeCount;

}
