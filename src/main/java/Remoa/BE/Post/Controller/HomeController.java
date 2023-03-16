package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Response.ResHomeReferenceDto;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    private final PostService postService;

    @GetMapping("/")
    public ResponseEntity<Object> home(HttpServletRequest request,
                                       @RequestParam(required = false, defaultValue = "all") String category,
                                       @RequestParam(required = false, defaultValue = "newest") String sort,
                                       @RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber) {

        Map<String, Object> responseData = new HashMap<>();

        pageNumber -= 1;

        if (authorized(request)) { //로그인이 된 상태. -> 내가 로그인했다는 걸 알려줄 수 있는 정보를 return해줘야함.
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            responseData.put("memberId", myMember.getMemberId());
            responseData.put("memberNickname", myMember.getNickname());

        } else {
            responseData.put("guest", null); //로그인하지 않은 사용자의 경우. 없어도 무방함.
        }

        Page<Post> allPosts;
        if (category.equals("idea") ||
                category.equals("marketing") ||
                category.equals("design") ||
                category.equals("video") ||
                category.equals("etc")) {

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
        } else {
            switch (sort) {
                case "newest":
                    log.warn("asdfasdfasdfasdfasdfasdf!!!!");
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
        }

        List<ResHomeReferenceDto> result = new ArrayList<>();
        log.warn("isit? = {}", allPosts.isEmpty());

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
}
