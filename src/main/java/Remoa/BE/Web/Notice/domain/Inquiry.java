package Remoa.BE.Web.Notice.domain;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    private String author;

    private String title;

    private String content;

    private LocalDateTime postingTime;

    private int view;

    public void addInquiryViewCount(int viewCount) {
        this.view = viewCount + 1;
    }

}
