package Remoa.BE.Post.Dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadPostForm {

    private String title; // Post name

    private String contestName;

    private String contestAwardType;

    private String category; // Category name

}
