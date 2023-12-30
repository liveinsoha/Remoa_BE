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
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private String title;

    private String content;

    private LocalDateTime postingTime;

    private int view;

    public void addNoticeViewCount(int viewCount) {
        this.view = viewCount + 1;
    }

}
