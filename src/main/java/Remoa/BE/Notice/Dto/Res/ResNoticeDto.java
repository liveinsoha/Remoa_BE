package Remoa.BE.Notice.Dto.Res;

import Remoa.BE.Notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class ResNoticeDto {

    private Long noticeId;
    private String title;
    private LocalDate postingTime;
    private int view ;

    public ResNoticeDto(Notice entity) {
        this.noticeId = entity.getNoticeId();
        this.title = entity.getTitle();
        this.postingTime = entity.getPostingTime().toLocalDate();
        this.view = entity.getView();
    }
}
