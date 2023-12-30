package Remoa.BE.Notice.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    private String title;

    private String content;

    private LocalDateTime postingTime;

    private Integer view;

    public void addInquiryViewCount(int viewCount) {
        this.view = viewCount + 1;
    }

}
