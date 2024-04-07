package Remoa.BE.Web.Post.Dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ResReferenceRegisterDto {

    @Schema(description = "게시물 ID", example = "1")
    private Long postId;

    @Schema(description = "게시물 제목", example = "서울 빅데이터 공모전 수상작")
    private String title;

    @Schema(description = "공모전명", example = "서울 빅데이터 공모전")
    private String contestName;

    @Schema(description = "카테고리", example = "빅데이터")
    private String category;

    @Schema(description = "수상 종류", example = "우수상")
    private String contestAwardType;

    @Schema(description = "유튜브 링크", example = "https://www.youtube.com/watch?v=video_id")
    private String youtubeLink;

    @Schema(description = "총 페이지 수", example = "10")
    private Integer pageCount;

    @Schema(description = "파일명 목록", example = "[\"file1.pdf\", \"file2.pdf\"]")
    private List<String> fileNames;
}