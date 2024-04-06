package Remoa.BE.Post.Dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResLikeFeedbackDto {

    @Schema(description = "좋아요 수", example = "10")
    private Integer likeCount;
}