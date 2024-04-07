package Remoa.BE.Web.Notice.Dto.Req;

import Remoa.BE.Web.Notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ReqNoticeDto {

    @Schema(description = "제목", example = "공지사항 제목")
    @NotNull
    private String title;

    @Schema(description = "내용", example = "이 공지사항은 중요한 내용을 포함합니다.")
    @NotNull
    private String content;

    public Notice toEntityNotice(String enrollNickname) {

        return Notice.builder()
                .author(enrollNickname)
                .title(title)
                .content(content)
                .postingTime(LocalDateTime.now())
                .view(0)
                .build();
    }


}
