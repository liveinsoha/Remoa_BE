package Remoa.BE.Web.Post.Dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "게시물 등록 양식")
public class UploadPostForm {

    @Schema(description = "게시물 제목", example = "서울 빅데이터 공모전 최우수상")
    private String title; // 게시물 제목

    @Schema(description = "공모전명", example = "서울 빅데이터 공모전")
    private String contestName; // 공모전명

    @Schema(description = "수상 유형", example = "최우수상")
    private String contestAwardType; // 수상 유형

    @Schema(description = "카테고리", example = "etc")
    private String category; // 카테고리 이름

    @Schema(description = "YouTube 링크", example = "https://www.youtube.com/watch?v=video_id")
    private String youtubeLink; // YouTube 링크

}
