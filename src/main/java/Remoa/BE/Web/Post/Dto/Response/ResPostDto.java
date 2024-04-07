package Remoa.BE.Web.Post.Dto.Response;

import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 작업물 목록을 보여줄 때 쓰일 Post의 간단한 정보만을 담은 Dto.
 */
@Setter
@Getter
@Builder
public class ResPostDto {


    @Schema(description = "게시물 ID", example = "12345")
    public Long postId;

    @Schema(description = "게시물 작성자 정보")
    public ResMemberInfoDto postMember;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    public String thumbnail;

    @Schema(description = "게시물 제목", example = "이것이 게시물 제목입니다.")
    public String title;

    @Schema(description = "좋아요 수", example = "100")
    public Integer likeCount;

    @Schema(description = "현재 사용자가 해당 게시물을 좋아하는지 여부", example = "true")
    public Boolean isLikedPost;

    @Schema(description = "게시물 작성 시간", example = "2024-04-05T08:30:00Z")
    public String postingTime;

    @Schema(description = "조회수", example = "500")
    public Integer views;

    @Schema(description = "스크랩 수", example = "50")
    public Integer scrapCount;

    @Schema(description = "현재 사용자가 해당 게시물을 스크랩했는지 여부", example = "false")
    public Boolean isScrapedPost;

    @Schema(description = "게시물 카테고리 이름", example = "기술")
    public String categoryName;

}
