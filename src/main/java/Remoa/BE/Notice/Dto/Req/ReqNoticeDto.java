package Remoa.BE.Notice.Dto.Req;

import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ReqNoticeDto {

    @NotNull
    private String title;

    @NotNull
    private String content;

    public Notice toEntityNotice() {
        return Notice.builder()
                .title(title)
                .content(content)
                .postingTime(LocalDateTime.now())
                .view(0)
                .build();
    }

    public Inquiry toEntityInquiry() {
        return Inquiry.builder()
                .title(title)
                .content(content)
                .postingTime(LocalDateTime.now())
                .view(0)
                .build();
    }
}
