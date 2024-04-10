package Remoa.BE.Web.Inquiry.Domain;


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
@SQLDelete(sql = "UPDATE notice SET deleted = true WHERE notice_id = ?")
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
}
