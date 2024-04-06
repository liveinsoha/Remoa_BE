package Remoa.BE.Post.Dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LikePostResponseDto {

    @Schema(description = "좋아요 수", example = "10")
    private Integer likeCount;
}