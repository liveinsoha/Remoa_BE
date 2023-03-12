package Remoa.BE.Post.Controller;

import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Dto.Response.ResReferenceRegisterDto;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {


    private final PostService postService;


    @PostMapping("/reference")  // 게시물 등록
    public ResponseEntity<Object> share(@RequestPart("data") UploadPostForm uploadPostForm,
                                        @RequestPart("thumbnail")MultipartFile thumbnail,
                                        @RequestPart("file") List<MultipartFile> uploadFiles, HttpServletRequest request) throws IOException {
        if(authorized(request)){
            Long memberId = getMemberId();

            Post savePost = postService.registerPost(uploadPostForm,thumbnail,uploadFiles,memberId);

            //잘못된 파일 유형
            if(savePost == null){
                return errorResponse(CustomMessage.BAD_FILE);
            }

            Post post = postService.findOne(savePost.getPostId());

            ResReferenceRegisterDto resReferenceRegisterDto = ResReferenceRegisterDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .category(post.getCategory().getName())
                    .contestAwardType(post.getContestAwareType())
                    .contestName(post.getContestName())
                    .pageCount(post.getPageCount())
                    .fileNames(post.getUploadFiles().stream().map(UploadFile::getOriginalFileName).collect(Collectors.toList()))
                    .build();
            return successResponse(CustomMessage.OK, resReferenceRegisterDto);


        }

        return errorResponse(CustomMessage.UNAUTHORIZED);
    }



}