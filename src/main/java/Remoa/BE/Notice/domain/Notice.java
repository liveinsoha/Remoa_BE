package Remoa.BE.Notice.domain;

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
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private String author;

    private String title;

    private String content;

    private LocalDateTime postingTime;

    private int view;

    public void addNoticeViewCount() {
        this.view = this.view + 1;
    }

}
