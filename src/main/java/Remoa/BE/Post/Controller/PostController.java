package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Dto.Response.ResPostDto;
import Remoa.BE.Post.Dto.Response.ResReferenceRegisterDto;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.modelmapper.convention.MatchingStrategies;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
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
    private final ModelMapper modelMapper;


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

    //게시물 조회
    @GetMapping("/reference")
    public ResponseEntity<Object> searchPost(@RequestParam String name) {


        List<Post> posts;
        if(name == null){
            posts = postService.findAll();
        }
        else{
            posts = postService.searchPost(name);
        }

        List<ResPostDto> result = new ArrayList<>();
        for (Post post : posts){

            ResPostDto map = ResPostDto.builder()
                    .postingTime(post.getPostingTime().toString())
                    .postMember(new ResMemberInfoDto(post.getMember().getMemberId(), post.getMember().getNickname(), post.getMember().getProfileImage()))
                    .postId(post.getPostId())
                    .views(post.getViews())
                    .categoryName(post.getCategory().getName())
                    .likeCount(post.getLikeCount())
                    .thumbnail(post.getThumbnail().getStoreFileUrl())
                    .scrapCount(post.getScrapCount())
                    .title(post.getTitle()).build();
            result.add(map);
       }

        return successResponse(CustomMessage.OK,result);
    }


}