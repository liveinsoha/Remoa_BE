package Remoa.BE.Notice.Dto.Req;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.utill.MemberInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ReqNoticeDto {

    @NotNull
    private String title;

    @NotNull
    private String content;

    public Notice toEntityNotice() {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Notice.builder()
                .author(member.getNickname())
                .title(title)
                .content(content)
                .postingTime(LocalDateTime.now())
                .view(0)
                .build();
    }

    public Inquiry toEntityInquiry() {
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Inquiry.builder()
                .author(member.getNickname())
                .title(title)
                .content(content)
                .postingTime(LocalDateTime.now())
                .view(0)
                .build();
    }

}
