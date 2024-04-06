package Remoa.BE.Notice.Dto.Res;


import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class ResAllInquiryDto {

    @Schema(description = "문의 ID", example = "1")
    private Long inquiryId;

    @Schema(description = "작성자", example = "Alice Smith")
    private String author;

    @Schema(description = "제목", example = "Product Inquiry")
    private String title;

    @Schema(description = "내용", example = "I have some questions regarding your product...")
    private String content;

    @Schema(description = "작성일", example = "2024-04-06")
    private LocalDate postingTime;

    @Schema(description = "조회 수", example = "50")
    private int view;

    public ResAllInquiryDto(Inquiry entity) {
        this.inquiryId = entity.getInquiryId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.postingTime = entity.getPostingTime().toLocalDate();
        this.view = entity.getView();
    }
}
