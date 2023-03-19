package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Service.FeedbackService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MemberService memberService;

    @PostMapping("/reference/{reference_id}/{page_number}") // 레퍼런스에 피드백 등록
    public ResponseEntity<Object> registerFeedback(@RequestBody Map<String, String> feedback,
                                                   @PathVariable("reference_id") Long postId,
                                                   @PathVariable("page_number") Integer pageNumber,
                                                   HttpServletRequest request){
        String myFeedback = feedback.get("feedback");
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            feedbackService.registerFeedback(myMember, myFeedback, postId, pageNumber, null);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PostMapping("/reference/{reference_id}/feedback/{feedback_id}") // 레퍼런스 피드백 대댓글
    public ResponseEntity<Object> registerFeedbackReply(@RequestBody Map<String, String> feedback,
                                                   @PathVariable("reference_id") Long postId,
                                                   @PathVariable("feedback_id") Long feedbackId,
                                                   HttpServletRequest request){
        String myFeedback = feedback.get("feedback")
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            feedbackService.registerFeedback(myMember, myFeedback, postId, null, feedbackId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PutMapping("/reference/feedback/{feedback_id}") // 피드백 수정
    public ResponseEntity<Object> modifyFeedback(@RequestBody Map<String, String> feedback, @PathVariable("feedback_id") Long feedbackId, HttpServletRequest request){
        String myFeedback = feedback.get("feedback");

        if(authorized(request)){
            feedbackService.modifyFeedback(myFeedback, feedbackId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @DeleteMapping("/reference/feedback/{feedback_id}")
    public ResponseEntity<Object> deleteFeedback(@PathVariable("feedback_id") Long feedbackId, HttpServletRequest request){
        if(authorized(request)){

            feedbackService.deleteFeedback(feedbackId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PostMapping("/reference/feedback/{feedback_id}/like") // 피드백 좋아요
    public ResponseEntity<Object> likeFeedback(@PathVariable("feedback_id") Long feedbackId, HttpServletRequest request){
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            feedbackService.likeFeedback(memberId, myMember, feedbackId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);

    }
}