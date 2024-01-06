package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import lombok.*;

/**
 * 작업물 목록을 보여줄 때 쓰일 Post의 간단한 정보만을 담은 Dto.
 */
@Setter
@Getter
@Builder
public class ResPostDto {

    public Long postId;
    public ResMemberInfoDto postMember;
    public String thumbnail;
    public String title;
    public Integer likeCount;
    public Boolean isLikedPost;
    public String postingTime;
    public Integer views;
    public Integer scrapCount;
    public Boolean isScrapedPost;
    public String categoryName;

}
