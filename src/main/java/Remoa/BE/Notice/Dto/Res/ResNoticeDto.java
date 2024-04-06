package Remoa.BE.Notice.Dto.Res;

import Remoa.BE.Notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class ResNoticeDto {

    @Schema(description = "게시물 ID", example = "1")
    private Long noticeId;

    @Schema(description = "작성자", example = "John Doe")
    private String author;

    @Schema(description = "제목", example = "공지사항 제목")
    private String title;

    @Schema(description = "작성 시간", example = "2023-04-01")
    private LocalDate postingTime;

    @Schema(description = "조회수", example = "100")
    private int view;

    public ResNoticeDto(Notice entity) {
        this.noticeId = entity.getNoticeId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.postingTime = entity.getPostingTime().toLocalDate();
        this.view = entity.getView();
    }
}
