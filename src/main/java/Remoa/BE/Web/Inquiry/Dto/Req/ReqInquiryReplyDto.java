package Remoa.BE.Web.Inquiry.Dto.Req;

import Remoa.BE.Web.Inquiry.Domain.Inquiry;
import Remoa.BE.Web.Inquiry.Domain.InquiryReply;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqInquiryReplyDto {

    @Schema(description = "답글 제목", example = "문의사항 답글 제목")
    @NotNull
    private String replyTitle;

    @Schema(description = "답글 내용", example = "이 문의사항 답글 내용이 이거입니다.")
    @NotNull
    private String replyContent;

    public InquiryReply toEntityInquiryReply(String enrollNickname, Inquiry inquiry) {
        return InquiryReply.builder()
                .inquiry(inquiry)
                .author(enrollNickname)
                .replyTitle(replyTitle)
                .replyContent(replyContent)
                .postingTime(LocalDateTime.now())
                .build();
    }
}