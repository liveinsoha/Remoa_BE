package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Dto.Response.ThumbnailReferenceDto;
import Remoa.BE.Post.Service.CommentService;
import Remoa.BE.Post.Service.MyPostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
public class MyPostController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final MyPostService myPostService;

    // Entity <-> DTO 간의 변환을 편리하게 하고자 ModelMapper 사용.(build.gradle에 의존성 주입 완료)
    private final ModelMapper modelMapper = new ModelMapper();


    /**
     * 내 작업물 목록 페이지
     */
    @GetMapping("/user/reference")
    public ResponseEntity<Object> myReference(HttpServletRequest request) {

        if (authorized(request)) {
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);

            List<Post> allPosts = myPostService.showOnesPosts(myMember);
            List<ThumbnailReferenceDto> myReferenceList = new ArrayList<>();

            // ModelMapper initialize
            initModelMapper();

            for (Post post : allPosts) {
                // ModelMapper 통해 Entity -> DTO 변환
                ThumbnailReferenceDto postDTO = modelMapper.map(post, ThumbnailReferenceDto.class);

                myReferenceList.add(postDTO);
            }
            return successResponse(CustomMessage.OK, myReferenceList);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @GetMapping("/user/references")
    public ResponseEntity<Object> myReferencePaging(HttpServletRequest request,
                                                    @RequestParam int page,
                                                    @RequestParam String sort) {
        if (authorized(request)) {
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);

            Page<Post> posts;
            if (sort.equals("newest")) {
                posts = myPostService.getNewestPosts(page, myMember);
            } else if (sort.equals("oldest")) {
                posts = myPostService.getOldestPosts(page, myMember);
            } else if (sort.equals("like")) {
                posts = myPostService.getMostLikePosts(page, myMember);
            } else if (sort.equals("scrap")) {
                posts = myPostService.getMostScrapPosts(page, myMember);
            } else {
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                posts = myPostService.getNewestPosts(page, myMember);
            }

            if (posts.isEmpty()) {
                return errorResponse(CustomMessage.PAGE_NUM_OVER);
            }

            initModelMapper();

            List<ThumbnailReferenceDto> myReferenceList = new ArrayList<>();
            for (Post post : posts.getContent()) {
                // ModelMapper 통해 Entity -> DTO 변환
                ThumbnailReferenceDto postDTO = modelMapper.map(post, ThumbnailReferenceDto.class);

                myReferenceList.add(postDTO);
            }

            Map<String, Object> referencesAndPageInfo = new HashMap<>();
            referencesAndPageInfo.put("references", myReferenceList); //조회한 레퍼런스들
            referencesAndPageInfo.put("totalPages", posts.getTotalPages()); //전체 페이지의 수
            referencesAndPageInfo.put("totalOfAllReferences", posts.getTotalElements()); //모든 레퍼런스의 수
            referencesAndPageInfo.put("totalOfPageElements", posts.getNumberOfElements()); //현 페이지의 레퍼런스 수

            return successResponse(CustomMessage.OK, referencesAndPageInfo);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    /**
     * Post Entity -> Thumbnail용 Response DTO를 위한 ModelMapper.
     */
    private void initModelMapper() {

        modelMapper.typeMap(Post.class, ThumbnailReferenceDto.class)
                .addMappings(mapper -> mapper.using(
                                (Converter<Member, String>) context -> context.getSource().getNickname())
                        .map(Post::getMember, ThumbnailReferenceDto::setNickname))
                .addMappings(mapper -> mapper.using(
                        (Converter<List<UploadFile>, List<String>>) context -> context.getSource().stream()
                                .map(UploadFile::getStoreFileUrl).collect(Collectors.toList())
                ).map(Post::getUploadFiles, ThumbnailReferenceDto::setStoreFileUrls));
    }

}
