package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Dto.Response.ResHomeReferenceDto;
import Remoa.BE.Post.Dto.Response.ResReferenceRegisterDto;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//    private final ModelMapper modelMapper;

    @GetMapping("/reference")
    public ResponseEntity<Object> searchPost(@RequestParam(required = false, defaultValue = "all") String category,
                                             @RequestParam(required = false, defaultValue = "newest") String sort,
                                             @RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber) {

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
            allPosts = sortAndPaginatePostsByCategory(category, sort, pageNumber);
        } else {
            //sort -> 최신순 : newest, 좋아요순 : like, 스크랩순 : scrap, 조회순 : view
            allPosts = sortAndPaginatePosts(sort, pageNumber);
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
                            post.getMember().getProfileImage()))
                    .build();

            result.add(map);
        }

        responseData.put("references", result); //조회한 레퍼런스들
        responseData.put("totalPages", allPosts.getTotalPages()); //전체 페이지의 수
        responseData.put("totalOfAllReferences", allPosts.getTotalElements()); //모든 레퍼런스의 수
        responseData.put("totalOfPageElements", allPosts.getNumberOfElements()); //현 페이지의 레퍼런스 수

        return successResponse(CustomMessage.OK, responseData);
    }


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

    private Page<Post> sortAndPaginatePosts(String sort, int pageNumber) {
        Page<Post> allPosts;
        switch (sort) {
            case "newest":
                allPosts = postService.findAllPostsWithPaginationForHomepageNewest(pageNumber);
                break;
            case "like":
                allPosts = postService.findAllPostsWithPaginationForHomepageMostLiked(pageNumber);
                break;
            case "scrap":
                allPosts = postService.findAllPostsWithPaginationForHomepageMostScraped(pageNumber);
                break;
            case "view":
                allPosts = postService.findAllPostsWithPaginationForHomepageMostViewed(pageNumber);
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                allPosts = postService.findAllPostsWithPaginationForHomepageNewest(pageNumber);
                break;
        }
        return allPosts;
    }

    private Page<Post> sortAndPaginatePostsByCategory(String category, String sort, int pageNumber) {
        Page<Post> allPosts;
        switch (sort) {
            case "newest":
                allPosts = postService.findAllPostsWithPaginationForHomepageSortByCategoryNewest(pageNumber, category);
                break;
            case "like":
                allPosts = postService.findAllPostsWithPaginationForHomepageSortByCategoryMostLiked(pageNumber, category);
                break;
            case "scrap":
                allPosts = postService.findAllPostsWithPaginationForHomepageSortByCategoryMostScraped(pageNumber, category);
                break;
            case "view":
                allPosts = postService.findAllPostsWithPaginationForHomepageSortByCategoryMostViewed(pageNumber, category);
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                allPosts = postService.findAllPostsWithPaginationForHomepageSortByCategoryNewest(pageNumber, category);
                break;
        }
        return allPosts;
    }
}