package Remoa.BE.Web.MyPage.Dto.Res;

import Remoa.BE.Web.Post.Dto.Response.ResPostDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResMyScrapDto {

    @Schema(description = "스크랩한 게시글 리스트")
    private List<ResPostDto> posts;

    @Schema(description = "전체 페이지의 수")
    private int totalPages;

    @Schema(description = "모든 게시글의 수")
    private long totalOfAllPosts;

    @Schema(description = "현재 페이지의 게시글 수")
    private int totalOfPageElements;
}
