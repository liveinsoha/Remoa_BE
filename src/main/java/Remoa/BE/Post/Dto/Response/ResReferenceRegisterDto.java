package Remoa.BE.Post.Dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ResReferenceRegisterDto {

    private Long postId;

    private String title; // Post name

    private String contestName;

    private String category; // Category name

    private String contestAwardType;

    private Integer pageCount;

    private List<String> fileNames;
}
