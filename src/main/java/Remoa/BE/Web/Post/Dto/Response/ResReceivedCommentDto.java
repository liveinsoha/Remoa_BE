package Remoa.BE.Web.Post.Dto.Response;

import Remoa.BE.Web.Comment.Dto.Res.ResCommentDto;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ResReceivedCommentDto {

    private Long postId;
    private String thumbnail;
    private Map<String, ResCommentDto> commentInfo;
    private String title;

}
