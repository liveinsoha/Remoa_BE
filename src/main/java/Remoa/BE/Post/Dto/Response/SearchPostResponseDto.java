package Remoa.BE.Post.Dto.Response;

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
public class SearchPostResponseDto {

    @Schema(description = "검색된 레퍼런스 목록")
    private List<ResHomeReferenceDto> references;

    @Schema(description = "전체 페이지의 수", example = "5")
    private int totalPages;

    @Schema(description = "모든 레퍼런스의 수", example = "50")
    private long totalOfAllReferences;

    @Schema(description = "현재 페이지의 레퍼런스 수", example = "10")
    private int totalOfPageElements;

    // 생성자, 게터, 세터 등은 생략하였습니다.
}
