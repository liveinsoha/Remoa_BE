package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Dto.Response.ResHomeReferenceDto;
import Remoa.BE.Post.Dto.Response.ResReferenceRegisterDto;
import Remoa.BE.Post.Service.MyPostService;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.utill.CommonFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
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
    private final MyPostService myPostService;
    private final MemberService memberService;
    private final FollowService followService;
    private final ModelMapper modelMapper;

    @GetMapping("/reference")
    public ResponseEntity<Object> searchPost(HttpServletRequest request,
                                             @RequestParam(required = false, defaultValue = "all") String category,
                                             @RequestParam(required = false, defaultValue = "newest") String sort,
                                             @RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber,
                                             @RequestParam(required = false, defaultValue = "") String title) {

        Member myMember = null;
        if (authorized(request)) {
            Long myMemberId = getMemberId();
            myMember = memberService.findOne(myMemberId);
        }

        Map<String, Object> responseData = new HashMap<>();

        pageNumber -= 1;
        if (pageNumber < 0) {
            return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        Page<Post> allPosts;
        if (category.equals("idea") ||
                category.equals("marketing") ||
                category.equals("design") ||
                category.equals("video") ||
                category.equals("etc")) {
            //sort -> 최신순 : newest, 좋아요순 : like, 스크랩순 : scrap, 조회순 : view
            allPosts = postService.sortAndPaginatePostsByCategory(category, sort, pageNumber, title);
        } else {
            //sort -> 최신순 : newest, 좋아요순 : like, 스크랩순 : scrap, 조회순 : view
            allPosts = postService.sortAndPaginatePosts(sort, pageNumber, title);
        }

        if ((allPosts.getContent().isEmpty()) && (allPosts.getTotalElements() > 0)) {
            return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        List<ResHomeReferenceDto> result = new ArrayList<>();

        for (Post post : allPosts) {
            ResHomeReferenceDto map = ResHomeReferenceDto.builder()
                    .postThumbnail(post.getThumbnail().getStoreFileUrl())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .views(post.getViews())
                    .likeCount(post.getLikeCount())
                    .scrapCount(post.getScrapCount())
                    .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                            post.getMember().getNickname(),
                            post.getMember().getProfileImage(),
                            myMember != null ? followService.isMyMemberFollowMember(myMember, post.getMember()) : null))
                    .build();

            result.add(map);
        }

        responseData.put("references", result); //조회한 레퍼런스들
        responseData.put("totalPages", allPosts.getTotalPages()); //전체 페이지의 수
        responseData.put("totalOfAllReferences", allPosts.getTotalElements()); //모든 레퍼런스의 수
        responseData.put("totalOfPageElements", allPosts.getNumberOfElements()); //현 페이지의 레퍼런스 수

        return successResponse(CustomMessage.OK, responseData);
    }


    @PostMapping("/reference") // 게시물 등록
    public ResponseEntity<Object> share(@RequestPart("data") UploadPostForm uploadPostForm,
                                        @RequestPart("thumbnail")MultipartFile thumbnail,
                                        @RequestPart(value = "file", required = false) List<MultipartFile> uploadFiles,
                                        HttpServletRequest request) throws IOException {
        if (authorized(request)) {
            Long memberId = getMemberId();
            Post savePost = postService.registerPost(uploadPostForm, thumbnail, uploadFiles, memberId);
            Post post = postService.findOne(savePost.getPostId());

            ResReferenceRegisterDto resReferenceRegisterDto = ResReferenceRegisterDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .category(post.getCategory().getName())
                    .contestAwardType(post.getContestAwardType())
                    .contestName(post.getContestName())
                    .youtubeLink(post.getYoutubeLink())
                    .pageCount(post.getPageCount())
                    .build();
            if (post.getUploadFiles() != null) {
                resReferenceRegisterDto.setFileNames(post.getUploadFiles().stream()
                        .map(UploadFile::getOriginalFileName)
                        .collect(Collectors.toList()));
            }
            return successResponse(CustomMessage.OK, resReferenceRegisterDto);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PutMapping("/reference/{reference_id}") // 게시물 수정
    public ResponseEntity<Object> modify(@PathVariable("reference_id") Long referenceId,
                                       @RequestPart("data") UploadPostForm uploadPostForm,
                                       @RequestPart("thumbnail")MultipartFile thumbnail,
                                       @RequestPart(value = "file", required = false) List<MultipartFile> uploadFiles,
                                       HttpServletRequest request) throws IOException {
        if (authorized(request)) {
            Long memberId = getMemberId();
            Post post = postService.findOne(referenceId);
            if(memberId != post.getMember().getMemberId()){
                return errorResponse(CustomMessage.CAN_NOT_ACCESS);
            }
            Post modifiedPost = postService.modifyPost(uploadPostForm, thumbnail, uploadFiles, post);


            ResReferenceRegisterDto resReferenceRegisterDto = ResReferenceRegisterDto.builder()
                    .postId(modifiedPost.getPostId())
                    .title(modifiedPost.getTitle())
                    .category(modifiedPost.getCategory().getName())
                    .contestAwardType(modifiedPost.getContestAwardType())
                    .contestName(modifiedPost.getContestName())
                    .youtubeLink(modifiedPost.getYoutubeLink())
                    .pageCount(modifiedPost.getPageCount())
                    .build();
            if (modifiedPost.getUploadFiles() != null) {
                resReferenceRegisterDto.setFileNames(modifiedPost.getUploadFiles().stream()
                        .map(UploadFile::getOriginalFileName)
                        .collect(Collectors.toList()));
            }
            return successResponse(CustomMessage.OK, resReferenceRegisterDto);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }


    @PostMapping("/reference/{reference_id}/like")
    public ResponseEntity<Object> likeReference(@PathVariable("reference_id") Long referenceId, HttpServletRequest request) {
        if (authorized(request)) {
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            postService.likePost(memberId, myMember, referenceId);
            int count = postService.findLikeCount(referenceId);
            Map<String, Integer> map = Collections.singletonMap("likeCount", count);
            return successResponse(CustomMessage.OK,map);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PostMapping("/reference/{reference_id}/scrap")
    public ResponseEntity<Object> scrapReference(@PathVariable("reference_id") Long referenceId, HttpServletRequest request){
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            boolean isScrapAction = postService.scrapPost(memberId, myMember, referenceId);
            int count = postService.findScrapCount(referenceId);
            Map<String, Integer> map = Collections.singletonMap("scrapCount", count);
            // 스크랩의 경우 : 200 OK, 스크랩 해제의 경우 : 201 CREATED
            CustomMessage customMessage = isScrapAction ? CustomMessage.OK_SCRAP : CustomMessage.OK_UNSCRAP;
            return successResponse(customMessage, map);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @DeleteMapping("/user/reference/{reference_id}")
    public ResponseEntity<Object> deleteReference(@PathVariable("reference_id") Long[] postId, HttpServletRequest request){
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);

            // 현재 로그인한 사용자가 올린 게시글이 맞는지 확인/예외처리
            for(int i=0; i<postId.length;i++) {
                if (postService.checkMemberPost(myMember, postId[i])) {
                    postService.deleteReference(postId[i]);

                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.CAN_NOT_ACCESS);
    }

    @DeleteMapping("/user/referenceCatagory/{category}")
    public ResponseEntity<Object> deleteReferenceCategory(@PathVariable("category") String category, HttpServletRequest request){
        if(authorized(request)){
            Long categoryId = CommonFunction.getCategoryId(category); // 카테고리 id 추출.
            Long memberId = getMemberId();
            myPostService.deleteReferenceCategory(memberId,categoryId);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.CAN_NOT_ACCESS);
    }




}