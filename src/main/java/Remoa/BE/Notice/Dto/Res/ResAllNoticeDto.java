package Remoa.BE.Notice.Dto.Res;


import Remoa.BE.Notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class ResAllNoticeDto {


    @Schema(description = "공지 ID", example = "1")
    private Long noticeId;

    @Schema(description = "작성자", example = "John Doe")
    private String author;

    @Schema(description = "제목", example = "Important Announcement")
    private String title;

    @Schema(description = "내용", example = "This is an important announcement regarding...")
    private String content;

    @Schema(description = "작성일", example = "2024-04-06")
    private LocalDate postingTime;

    @Schema(description = "조회 수", example = "100")
    private int view;

    public ResAllNoticeDto(Notice entity) {
        this.noticeId = entity.getNoticeId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.postingTime = entity.getPostingTime().toLocalDate();
        this.view = entity.getView();
    }
}
