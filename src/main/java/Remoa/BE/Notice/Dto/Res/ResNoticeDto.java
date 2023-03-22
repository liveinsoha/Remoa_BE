package Remoa.BE.Notice.Dto.Res;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class ResNoticeDto {

    private Long noticeId;
    private String title;
    private LocalDateTime postingTime;
    private Integer view ;
}
