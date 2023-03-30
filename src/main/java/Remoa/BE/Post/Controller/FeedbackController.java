package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Feedback;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Request.ReqFeedbackDto;
import Remoa.BE.Post.Dto.Response.ResFeedbackDto;
import Remoa.BE.Post.Dto.Response.ResReplyDto;
import Remoa.BE.Post.Service.FeedbackService;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
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
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MemberService memberService;
    private final PostService postService;

    @PostMapping("/reference/{reference_id}/{page_number}") // 레퍼런스에 피드백 등록
    public ResponseEntity<Object> registerFeedback(@RequestBody ReqFeedbackDto req,
                                                   @PathVariable("reference_id") Long postId,
                                                   @PathVariable("page_number") Integer pageNumber,
                                                   HttpServletRequest request){
        String myFeedback = req.getFeedback();
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            Post post = postService.findOne(postId);
            // 피드백을 등록하려는 게시물의 페이지 수가 없을 경우 예외 처리
            if(post.getPageCount() < pageNumber || pageNumber < 1){
                return errorResponse(CustomMessage.BAD_PAGE_NUM);
            }

            feedbackService.registerFeedback(myMember, myFeedback, postId, pageNumber, null);
            List<ResFeedbackDto> feedbacks = feedbackService.findAllFeedbacksOfPost(post).stream()
                    .filter(feedback -> feedback.getParentFeedback() == null)
                    .map(feedback -> ResFeedbackDto.builder()
                            .feedbackId(feedback.getFeedbackId())
                            .member(new ResMemberInfoDto(feedback.getMember().getMemberId(),
                                    feedback.getMember().getNickname(),
                                    feedback.getMember().getProfileImage(),
                                    null))
                            .feedback(feedback.getFeedback())
                            .page(feedback.getPageNumber())
                            .likeCount(feedback.getFeedbackLikeCount())
                            .feedbackTime(feedback.getFeedbackTime())
                            //아래부터 대댓글 feedback 조회 및 dto 매핑
                            .replies(feedbackService.getParentFeedbacksReply(feedback).stream()
                                    .map(reply -> ResReplyDto.builder()
                                            .replyId(reply.getFeedbackId())
                                            .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                    reply.getMember().getNickname(),
                                                    reply.getMember().getProfileImage(),
                                                    null))
                                            .content(reply.getFeedback())
                                            .likeCount(reply.getFeedbackLikeCount())
                                            .repliedTime(reply.getFeedbackTime())
                                            .build()).collect(Collectors.toList()))
                            .build()).collect(Collectors.toList());
            return successResponse(CustomMessage.OK,feedbacks);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PostMapping("/reference/{reference_id}/feedback/{feedback_id}") // 레퍼런스에 피드백 대댓글 등록
    public ResponseEntity<Object> registerFeedbackReply(@RequestBody ReqFeedbackDto req,
                                                   @PathVariable("reference_id") Long postId,
                                                   @PathVariable("feedback_id") Long feedbackId,
                                                   HttpServletRequest request){
        String myFeedback = req.getFeedback();
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            Post post = postService.findOne(postId);
            feedbackService.registerFeedback(myMember, myFeedback, postId, null, feedbackId);

            List<ResFeedbackDto> feedbacks = feedbackService.findAllFeedbacksOfPost(post).stream()
                    .filter(feedback -> feedback.getParentFeedback() == null)
                    .map(feedback -> ResFeedbackDto.builder()
                            .feedbackId(feedback.getFeedbackId())
                            .member(new ResMemberInfoDto(feedback.getMember().getMemberId(),
                                    feedback.getMember().getNickname(),
                                    feedback.getMember().getProfileImage(),
                                    null))
                            .feedback(feedback.getFeedback())
                            .page(feedback.getPageNumber())
                            .likeCount(feedback.getFeedbackLikeCount())
                            .feedbackTime(feedback.getFeedbackTime())
                            //아래부터 대댓글 feedback 조회 및 dto 매핑
                            .replies(feedbackService.getParentFeedbacksReply(feedback).stream()
                                    .map(reply -> ResReplyDto.builder()
                                            .replyId(reply.getFeedbackId())
                                            .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                    reply.getMember().getNickname(),
                                                    reply.getMember().getProfileImage(),
                                                    null))
                                            .content(reply.getFeedback())
                                            .likeCount(reply.getFeedbackLikeCount())
                                            .repliedTime(reply.getFeedbackTime())
                                            .build()).collect(Collectors.toList()))
                            .build()).collect(Collectors.toList());
            return successResponse(CustomMessage.OK,feedbacks);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PutMapping("/reference/feedback/{feedback_id}") // 피드백 수정
    public ResponseEntity<Object> modifyFeedback(@RequestBody ReqFeedbackDto req, @PathVariable("feedback_id") Long feedbackId, HttpServletRequest request){
        String myFeedback = req.getFeedback();

        if(authorized(request)){
            feedbackService.modifyFeedback(myFeedback, feedbackId);
            Feedback f = feedbackService.findOne(feedbackId);
            List<ResFeedbackDto> feedbacks = feedbackService.findAllFeedbacksOfPost(f.getPost()).stream()
                    .filter(feedback -> feedback.getParentFeedback() == null)
                    .map(feedback -> ResFeedbackDto.builder()
                            .feedbackId(feedback.getFeedbackId())
                            .member(new ResMemberInfoDto(feedback.getMember().getMemberId(),
                                    feedback.getMember().getNickname(),
                                    feedback.getMember().getProfileImage(),
                                    null))
                            .feedback(feedback.getFeedback())
                            .page(feedback.getPageNumber())
                            .likeCount(feedback.getFeedbackLikeCount())
                            .feedbackTime(feedback.getFeedbackTime())
                            //아래부터 대댓글 feedback 조회 및 dto 매핑
                            .replies(feedbackService.getParentFeedbacksReply(feedback).stream()
                                    .map(reply -> ResReplyDto.builder()
                                            .replyId(reply.getFeedbackId())
                                            .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                    reply.getMember().getNickname(),
                                                    reply.getMember().getProfileImage(),
                                                    null))
                                            .content(reply.getFeedback())
                                            .likeCount(reply.getFeedbackLikeCount())
                                            .repliedTime(reply.getFeedbackTime())
                                            .build()).collect(Collectors.toList()))
                            .build()).collect(Collectors.toList());
            return successResponse(CustomMessage.OK,feedbacks);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @DeleteMapping("/reference/feedback/{feedback_id}")
    public ResponseEntity<Object> deleteFeedback(@PathVariable("feedback_id") Long feedbackId, HttpServletRequest request){
        if(authorized(request)){
            feedbackService.deleteFeedback(feedbackId);

            Feedback f = feedbackService.findOne(feedbackId);
            List<ResFeedbackDto> feedbacks = feedbackService.findAllFeedbacksOfPost(f.getPost()).stream()
                    .filter(feedback -> feedback.getParentFeedback() == null)
                    .map(feedback -> ResFeedbackDto.builder()
                            .feedbackId(feedback.getFeedbackId())
                            .member(new ResMemberInfoDto(feedback.getMember().getMemberId(),
                                    feedback.getMember().getNickname(),
                                    feedback.getMember().getProfileImage(),
                                    null))
                            .feedback(feedback.getFeedback())
                            .page(feedback.getPageNumber())
                            .likeCount(feedback.getFeedbackLikeCount())
                            .feedbackTime(feedback.getFeedbackTime())
                            //아래부터 대댓글 feedback 조회 및 dto 매핑
                            .replies(feedbackService.getParentFeedbacksReply(feedback).stream()
                                    .map(reply -> ResReplyDto.builder()
                                            .replyId(reply.getFeedbackId())
                                            .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                    reply.getMember().getNickname(),
                                                    reply.getMember().getProfileImage(),
                                                    null))
                                            .content(reply.getFeedback())
                                            .likeCount(reply.getFeedbackLikeCount())
                                            .repliedTime(reply.getFeedbackTime())
                                            .build()).collect(Collectors.toList()))
                            .build()).collect(Collectors.toList());
            return successResponse(CustomMessage.OK,feedbacks);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PostMapping("/reference/feedback/{feedback_id}/like") // 피드백 좋아요
    public ResponseEntity<Object> likeFeedback(@PathVariable("feedback_id") Long feedbackId, HttpServletRequest request){
        if(authorized(request)){
            Long memberId = getMemberId();
            Member myMember = memberService.findOne(memberId);
            feedbackService.likeFeedback(memberId, myMember, feedbackId);
            int count = feedbackService.feedbackLikeCount(feedbackId);
            Map<String, Integer> map = Collections.singletonMap("LikeCount", count);
            return successResponse(CustomMessage.OK,map);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);

    }
}