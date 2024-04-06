package Remoa.BE.Post.Dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ScrapReferenceResponseDto {

    @Schema(description = "스크랩 수", example = "10")
    private Integer scrapCount;
}