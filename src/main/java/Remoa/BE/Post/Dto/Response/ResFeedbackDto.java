package Remoa.BE.Post.Dto.Response;

import lombok.Builder;

@Builder
public class ResFeedbackDto {
    public Long feedbackId;
    public Integer pageNumber;
    public String feedback;
    public String feedbackTime;
}