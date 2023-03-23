package Remoa.BE.Post.Dto.Response;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResReferenceViewerDto {

    public Long postId;
    public ResMemberInfoDto postMember;
    public String thumbnail;
    private String contestName;
    private String contestAwardType;
    private String category;
    public String title;
    public Integer likeCount;
    public Integer scrapCount;
    public String postingTime;
    public Integer views;
    private Integer pageCount;
    private List<String> fileNames;
    private List<ResCommentDto> comments;
    private List<ResFeedbackDto> feedbacks;

}
