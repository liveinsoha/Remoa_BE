package Remoa.BE.Notice.Dto.Res;

import Remoa.BE.Notice.domain.Inquiry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class ResInquiryDto {

    private Long noticeId;
    private String author;
    private String title;
    private LocalDate postingTime;
    private int view;

    public ResInquiryDto(Inquiry inquiry) {
        this.noticeId = inquiry.getInquiryId();
        this.author = inquiry.getAuthor();
        this.title = inquiry.getTitle();
        this.postingTime = inquiry.getPostingTime().toLocalDate();
        this.view = inquiry.getView();
    }

}
