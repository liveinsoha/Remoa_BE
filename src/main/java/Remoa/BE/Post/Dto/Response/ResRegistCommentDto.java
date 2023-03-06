package Remoa.BE.Post.Dto.Response;

import lombok.Builder;

@Builder
public class ResRegistCommentDto {
    public Long commentId;
    public String comment;
    public String commentedTime;
    public Integer commentLikeCount;
    public Boolean deleted = Boolean.FALSE;

}
