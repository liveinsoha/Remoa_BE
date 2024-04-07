package Remoa.BE.Web.Post.Dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostPageResponseDto {

    @Schema(description = "게시물 레퍼런스 리스트")
    private List<ResPostDto> references;

    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    @Schema(description = "전체 레퍼런스 수", example = "50")
    private long totalOfAllReferences;

    @Schema(description = "현재 페이지의 레퍼런스 수", example = "10")
    private int totalOfPageElements;
}
