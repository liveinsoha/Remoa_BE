package Remoa.BE.Post.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 작업물 목록을 보여줄 때 쓰일 Post의 간단한 정보만을 담은 Dto.
 */
@Data
@Builder
public class ThumbnailReferenceDto {

    //TODO 피그마를 통해 분석해보면 썸네일로 쓰일 Member의 profileImage 필요함. 또, 공유횟수도 추가해야하는지 확인 필요

    public Long postId;
    public String nickname;
    public String title;
//    public String contestName;
//    public String deadline;
//    public Boolean ContestAward;
//    public String contestAwareType;
    public Integer likeCount;
    public String postingTime;
    public Integer views;
    public Integer scrapCount;
//    public Boolean deleted;
    public List<String> storeFileUrls;
    public String categoryName;

}
