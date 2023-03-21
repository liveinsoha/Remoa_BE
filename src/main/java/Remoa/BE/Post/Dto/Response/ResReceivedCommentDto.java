package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResReceivedCommentDto {

    private Long postId;
    private Map<String, ResCommentDto> commentInfo;
    private String title;

}
