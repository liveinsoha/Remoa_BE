package Remoa.BE.Web.Inquiry.Domain;


import Remoa.BE.Web.Inquiry.Dto.Req.ReqInquiryDto;
import Remoa.BE.Web.Inquiry.Dto.Req.ReqInquiryReplyDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE inquiry_reply_id SET deleted = true WHERE inquiry_reply_id = ?")
public class InquiryReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long inquiryReplyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    Inquiry inquiry;

    private String author;

    private String replyTitle;

    private String replyContent;

    private LocalDateTime postingTime;

    private LocalDateTime modifiedTime;

    @Builder.Default
    private Boolean modified = Boolean.FALSE;

    @Builder.Default
    private Boolean deleted = Boolean.FALSE;

    public void updateInquiry(ReqInquiryReplyDto updateReplyDto) {
        this.replyTitle = updateReplyDto.getReplyTitle();
        this.replyContent = updateReplyDto.getReplyContent();
        this.modified = true;
        this.modifiedTime = LocalDateTime.now();
        // 필요한 경우 다른 필드도 업데이트할 수 있습니다.
    }
}
