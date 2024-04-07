package Remoa.BE.Web.Notice.Dto.Res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class NoticeResponseDto {

    @Schema(description = "게시물 목록")
    private List<ResNoticeDto> notices;

    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    @Schema(description = "전체 게시물 수", example = "100")
    private long totalOfAllNotices;

    @Schema(description = "현재 페이지의 게시물 수", example = "10")
    private int totalOfPageElements;

    // 생성자, getter, setter 등
}
