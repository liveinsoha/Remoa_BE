package Remoa.BE.Notice.Dto.Req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReqNoticeDto {

    @NotNull
    private String title;

    @NotNull
    private String content;
}
