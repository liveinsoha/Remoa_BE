package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Service.FeedBackService;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedBackController {

    private final FeedBackService feedbackService;
    private final MemberService memberService;

    private final PostService postService;

    @PostMapping("/reference/{reference_id}/{page_number}") // 레퍼런스에 피드백 등록
    public ResponseEntity<Object> registFeedback(@RequestBody Map<String, String> feedback, @PathVariable("reference_id") Long postId, @PathVariable("page_number") Integer pageNumber, HttpServletRequest request){
        String myFeedback = feedback.get("feedback");
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            Post post = postService.findOne(postId);
            Integer pageNum = post.getPageCount();

            if(pageNumber > pageNum || pageNumber < 1){ // 등록하려는 feedback의 페이지 수가 잘못되었을 경우
                return errorResponse(CustomMessage.BAD_PAGE_NUM);
            }
            feedbackService.registFeedback(myMember, myFeedback, postId, pageNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }
}