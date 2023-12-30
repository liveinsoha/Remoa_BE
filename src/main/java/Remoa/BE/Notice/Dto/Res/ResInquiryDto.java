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

    private Long inquiryId;
    private String title;
    private LocalDate postingTime;
    private Integer view;

    public ResInquiryDto(Inquiry inquiry) {
        this.inquiryId = inquiry.getInquiryId();
        this.title = inquiry.getTitle();
        this.postingTime = inquiry.getPostingTime().toLocalDate();
        this.view = inquiry.getView();
    }

}
