package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Service.FileService;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Dto.Response.ResReferenceDto;
import Remoa.BE.Post.Dto.Response.ResRegistCommentDto;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {


    private final PostService postService;

    private final MemberService memberService;


    @PostMapping("/reference")  // 게시물 등록
    public ResponseEntity<Object> share(@RequestPart("data") UploadPostForm uploadPostForm,
                      @RequestParam("file") MultipartFile uploadFile, HttpServletRequest request){
        //TODO postingTime 설정. 로그인 여부 거르는 건 Spring Security 설정으로 가능해서 우선 없어도 괜찮을듯함.
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            Post post = postService.registerPost(uploadPostForm,uploadFile,myMember);

            ResReferenceDto resReferenceDto = ResReferenceDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .category(post.getCategory().getName())
                    .contestAwardType(post.getContestAwareType())
                    .contestName(post.getContestName())
                    .fileName(post.getUploadFile().getOriginalFileName())
                    .build();
            myMember.getPosts().add(post);

            return successResponse(CustomMessage.OK,resReferenceDto);
        }

        return errorResponse(CustomMessage.UNAUTHORIZED);
    }



}