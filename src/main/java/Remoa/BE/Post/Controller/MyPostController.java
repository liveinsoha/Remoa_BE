package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Response.ResPostDto;
import Remoa.BE.Post.Service.CommentService;
import Remoa.BE.Post.Service.MyPostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyPostController {

    private final MemberService memberService;
    private final MyPostService myPostService;
    private final FollowService followService;

    // Entity <-> DTO 간의 변환을 편리하게 하고자 ModelMapper 사용.(build.gradle에 의존성 주입 완료)
//    private final ModelMapper modelMapper = new ModelMapper();


    /**
     * 내 작업물 목록 페이지
     */
    @GetMapping("/user/reference")
    public ResponseEntity<Object> myReference(HttpServletRequest request,
                                              @RequestParam(required = false, defaultValue = "all") String category,
                                              @RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber,
                                              @RequestParam(required = false, defaultValue = "newest") String sort,
                                              @RequestParam(required = false, defaultValue = "") String title) {
        if (authorized(request)) {
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);

            pageNumber -= 1;
            if (pageNumber < 0) {
                return errorResponse(CustomMessage.PAGE_NUM_OVER);
            }

            Page<Post> posts;

            if (category.equals("idea") ||
                    category.equals("marketing") ||
                    category.equals("design") ||
                    category.equals("video") ||
                    category.equals("etc")) {
                posts = myPostService.sortAndPaginatePostsByCategoryAndMember(category, pageNumber, sort, myMember,title);
            } else {
                posts = myPostService.sortAndPaginatePostsByMember(pageNumber, sort, myMember,title);
            }

            //조회할 레퍼런스가 db에 있으나, 현재 페이지에 조회할 데이터가 없는 경우 == 페이지 번호를 잘못 입력
            if ((posts.getContent().isEmpty()) && (posts.getTotalElements() > 0)) {
                return errorResponse(CustomMessage.PAGE_NUM_OVER);
            }

            List<ResPostDto> result = new ArrayList<>();

            for (Post post : posts) {
                ResPostDto map = ResPostDto.builder()
                        .postingTime(post.getPostingTime().toString())
                        .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                                post.getMember().getNickname(),
                                post.getMember().getProfileImage(),
                                null))
                        .postId(post.getPostId())
                        .views(post.getViews())
                        .categoryName(post.getCategory().getName())
                        .likeCount(post.getLikeCount())
                        .thumbnail(post.getThumbnail().getStoreFileUrl())
                        .scrapCount(post.getScrapCount())
                        .title(post.getTitle()).build();
                result.add(map);
            }
            //프론트에서 쓰일 조회한 레퍼런스들과 페이지 관련한 값들 map에 담아서 return.
            Map<String, Object> referencesAndPageInfo = new HashMap<>();
            referencesAndPageInfo.put("references", result); //조회한 레퍼런스들
            referencesAndPageInfo.put("totalPages", posts.getTotalPages()); //전체 페이지의 수
            referencesAndPageInfo.put("totalOfAllReferences", posts.getTotalElements()); //모든 레퍼런스의 수
            referencesAndPageInfo.put("totalOfPageElements", posts.getNumberOfElements()); //현 페이지의 레퍼런스 수

            return successResponse(CustomMessage.OK, referencesAndPageInfo);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @GetMapping("/user/reference/{member_id}")
    public ResponseEntity<Object> otherReference(HttpServletRequest request,
                                                 @PathVariable("member_id") Long memberId,
                                                 @RequestParam(required = false, defaultValue = "all") String category,
                                                 @RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber,
                                                 @RequestParam(required = false, defaultValue = "newest") String sort,
                                                 @RequestParam(required = false, defaultValue = "") String title) {

        Member myMember = null;
        if (authorized(request)) {
            Long myMemberId = getMemberId();
            myMember = memberService.findOne(myMemberId);
        }


        Member selectedMember = memberService.findOne(memberId);

        pageNumber -= 1;
        if (pageNumber < 0) {
            return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        Page<Post> posts;

        if (category.equals("idea") ||
                category.equals("marketing") ||
                category.equals("design") ||
                category.equals("video") ||
                category.equals("etc")) {
            posts = myPostService.sortAndPaginatePostsByCategoryAndMember(category, pageNumber, sort, selectedMember, title);
        } else {
            posts = myPostService.sortAndPaginatePostsByMember(pageNumber, sort, selectedMember, title);
        }

        //조회할 레퍼런스가 db에 있으나, 현재 페이지에 조회할 데이터가 없는 경우 == 페이지 번호를 잘못 입력
        if ((posts.getContent().isEmpty()) && (posts.getTotalElements() > 0)) {
            return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        List<ResPostDto> result = new ArrayList<>();

        for (Post post : posts) {
            ResPostDto map = ResPostDto.builder()
                    .postingTime(post.getPostingTime().toString())
                    .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                            post.getMember().getNickname(),
                            post.getMember().getProfileImage(),
                            myMember != null ? followService.isMyMemberFollowMember(myMember, post.getMember()) : null))
                    .postId(post.getPostId())
                    .views(post.getViews())
                    .categoryName(post.getCategory().getName())
                    .likeCount(post.getLikeCount())
                    .thumbnail(post.getThumbnail().getStoreFileUrl())
                    .scrapCount(post.getScrapCount())
                    .title(post.getTitle()).build();
            result.add(map);
        }
        //프론트에서 쓰일 조회한 레퍼런스들과 페이지 관련한 값들 map에 담아서 return.
        Map<String, Object> referencesAndPageInfo = new HashMap<>();
        referencesAndPageInfo.put("references", result); //조회한 레퍼런스들
        referencesAndPageInfo.put("totalPages", posts.getTotalPages()); //전체 페이지의 수
        referencesAndPageInfo.put("totalOfAllReferences", posts.getTotalElements()); //모든 레퍼런스의 수
        referencesAndPageInfo.put("totalOfPageElements", posts.getNumberOfElements()); //현 페이지의 레퍼런스 수

        return successResponse(CustomMessage.OK, referencesAndPageInfo);
    }




}
