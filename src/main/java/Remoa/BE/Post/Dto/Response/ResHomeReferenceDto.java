package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResHomeReferenceDto {

    private String postThumbnail;
    private Long postId;
    private String title;
    private int views;
    private int likeCount;
    private int scrapCount;
    private ResMemberInfoDto postMember;
}
