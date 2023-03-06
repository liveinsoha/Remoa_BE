package Remoa.BE.Post.Dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResReferenceDto {

    private Long postId;

    private String title; // Post name

    private String contestName;

    private String category; // Category name

    private String contestAwardType;

    private String fileName;
}
