package Remoa.BE.Web.Post.Dto.Response;

import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
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
    private Boolean isLikedPost;
    private int scrapCount;
    private Boolean isScrapedPost;
    private ResMemberInfoDto postMember;
}
