package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Service.CommentService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;

    private final CommentService commentService;

    @PostMapping("/reference/{reference_id}/comment")
    public ResponseEntity<Object> registerComment(@RequestBody Map<String, String> comment, @PathVariable("reference_id") Long postId, HttpServletRequest request){
        String myComment = comment.get("comment");
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            commentService.registerComment(myMember, myComment, postId, null);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PostMapping("/reference/{reference_id}/comment/{comment_id}")
    public ResponseEntity<Object> registerCommentReply(@RequestBody Map<String, String> comment, @PathVariable("reference_id") Long postId, @PathVariable("comment_id") Long commentId, HttpServletRequest request){
        String myComment = comment.get("comment");
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            commentService.registerComment(myMember, myComment, postId, commentId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

}