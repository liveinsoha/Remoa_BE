package Remoa.BE.Web.Notice.Dto.Req;

import Remoa.BE.Web.Notice.domain.Inquiry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ReqInqueryDto {

    @Schema(description = "제목", example = "문의사항 제목")
    @NotNull
    private String title;

    @Schema(description = "내용", example = "이 문의사항은 내용이 이거입니다.")
    @NotNull
    private String content;

    public Inquiry toEntityInquiry(String enrollNickname) {
        return Inquiry.builder()
                .author(enrollNickname)
                .title(title)
                .content(content)
                .postingTime(LocalDateTime.now())
                .view(0)
                .build();
    }
}
