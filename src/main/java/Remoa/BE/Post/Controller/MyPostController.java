package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Response.ResPostDto;
import Remoa.BE.Post.Service.CommentService;
import Remoa.BE.Post.Service.MyPostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<Object> myReferences(HttpServletRequest request) {

        if (authorized(request)) {
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);

            List<Post> allPosts = myPostService.showOnesPosts(myMember);
            List<ResPostDto> result = new ArrayList<>();

            for (Post post : allPosts) {
                ResPostDto map = ResPostDto.builder()
                        .postingTime(post.getPostingTime())
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
            return successResponse(CustomMessage.OK, result);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }


}
