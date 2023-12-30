package Remoa.BE.Notice.Dto.Res;


import Remoa.BE.Notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class ResAllNoticeDto {

    private Long noticeId;
    private String author;
    private String title;
    private String content;
    private LocalDate postingTime;
    private int view ;

    public ResAllNoticeDto(Notice entity) {
        this.noticeId = entity.getNoticeId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.postingTime = entity.getPostingTime().toLocalDate();
        this.view = entity.getView();
    }
}
