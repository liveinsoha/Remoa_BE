package Remoa.BE.Notice.Dto.Req;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.utill.MemberInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;

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
