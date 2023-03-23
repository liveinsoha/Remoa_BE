package Remoa.BE.Notice.Dto.Res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class ResNoticeDto {

    private Long noticeId;
    private String title;
    private LocalDate postingTime;
    private Integer view ;
}
