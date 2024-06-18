package Remoa.BE.Web.Feedback.Dto;


import Remoa.BE.Web.Feedback.Domain.Feedback;
import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class ResFeedbackDto2 {

    @Schema(description = "피드백 작성자 정보")
    private ResMemberInfoDto member;

    @Schema(description = "해당 회원의 피드백 정보")
    private List<ResFeedbackInfoDto> feedbackInfos;

    public ResFeedbackDto2(ResMemberInfoDto member, List<ResFeedbackInfoDto> feedbackInfos) {
        this.member = member;
        this.feedbackInfos = feedbackInfos;
    }
}
