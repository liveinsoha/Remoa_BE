package Remoa.BE.Web.Post.Dto.Request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UploadPostForm {

    private String title; // Post name

    private String contestName;

    private String contestAwardType;

    private String category; // Category name

    private String youtubeLink; // 유튜브 링크
}
