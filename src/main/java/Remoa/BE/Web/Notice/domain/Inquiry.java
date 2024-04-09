package Remoa.BE.Web.Notice.domain;

import Remoa.BE.Web.Notice.Dto.Req.ReqInquiryDto;
import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE notice SET deleted = true WHERE notice_id = ?")
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    private String author;

    private String title;

    private String content;

    private LocalDateTime postingTime;

    private LocalDateTime modifiedTime;

    @Builder.Default
    private Boolean modified = Boolean.FALSE;

    @Builder.Default
    private Boolean deleted = Boolean.FALSE;

    private int view;

    public void addInquiryViewCount(int viewCount) {
        this.view = viewCount + 1;
    }

    public void updateInquiry(ReqInquiryDto updateDto) {
        this.title = updateDto.getTitle();
        this.content = updateDto.getContent();
        this.modified = true;
        this.modifiedTime = LocalDateTime.now();
        // 필요한 경우 다른 필드도 업데이트할 수 있습니다.
    }
}
