package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResFeedbackDto {
    private Long feedbackId;
    private ResMemberInfoDto member;
    private String feedback;
    private Integer page;
    private Integer likeCount;
    private LocalDateTime feedbackTime;
    private List<ResReplyDto> replies;
}
