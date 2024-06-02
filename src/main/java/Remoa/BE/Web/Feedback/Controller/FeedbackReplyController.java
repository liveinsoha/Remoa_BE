package Remoa.BE.Web.Feedback.Controller;

import Remoa.BE.Web.Feedback.Domain.FeedbackReply;
import Remoa.BE.Web.Feedback.Service.FeedbackReplyService;
import Remoa.BE.Web.Feedback.Service.FeedbackService;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.MemberUtils;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.Web.Post.Dto.Request.ReqFeedbackDto;
import Remoa.BE.Web.Feedback.Dto.ResFeedbackDto;
import Remoa.BE.Web.Post.Service.PostService;
import Remoa.BE.config.auth.MemberDetails;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import Remoa.BE.exception.response.BaseResponse;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Tag(name = "레퍼런스 피드백 대댓글 기능 Test Completed", description = "레퍼런스 피드백 대댓글 기능 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedbackReplyController {

    private final FeedbackService feedbackService;
    private final MemberService memberService;
    private final PostService postService;
    private final FeedbackReplyService feedbackReplyService;
    private final MemberUtils memberUtils;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 대댓글을 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/{reference_id}/feedback/{feedback_id}") // 레퍼런스에 피드백 대댓글 등록
    @Operation(summary = "피드백 대댓글 등록 Test Completed", description = "특정 피드백에 대댓글을 등록합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> registerFeedbackReply(@RequestBody ReqFeedbackDto req,
                                                                                    @PathVariable("reference_id") Long postId,
                                                                                    @PathVariable("feedback_id") Long feedbackId,
                                                                                    @AuthenticationPrincipal MemberDetails memberDetails) {
        log.info("EndPoint Post /reference/{reference_id}/feedback/{feedback_id}");

        String content = req.getFeedback();
        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        feedbackReplyService.registerFeedbackReply(myMember, postId, feedbackId, content);

        // 조회한 post의 feedback 조회 및 각 feedback에 대한 feedbackReply 조회 -> 이후 ResFeedbackDto로 매핑
        List<ResFeedbackDto> resFeedbackDtos = memberUtils.feedbackList(postId, myMember);

        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, resFeedbackDtos);
        return ResponseEntity.ok(response);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 대댓글을 성공적으로 수정했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reference/feedback/{feedback_id}/reply/{reply_id}") // 피드백 대댓글 수정
    @Operation(summary = "피드백 대댓글 수정 Test Completed", description = "작성한 피드백 대댓글을 수정합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> modifyFeedbackReply(@RequestBody ReqFeedbackDto req,
                                                                                  @PathVariable("feedback_id") Long feedbackId,
                                                                                  @PathVariable("reply_id") Long replyId,
                                                                                  @AuthenticationPrincipal MemberDetails memberDetails) {
        log.info("EndPoint Put /reference/feedback/{feedback_id}/reply/{reply_id}");

        String content = req.getFeedback();
        FeedbackReply reply = feedbackReplyService.findOne(replyId);
        Long myMemberId = memberDetails.getMemberId();
        if (!Objects.equals(reply.getMember().getMemberId(), myMemberId)) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
        }

        feedbackReplyService.modifyFeedbackReply(content, replyId);

        Member myMember = memberService.findOne(myMemberId);
        // 조회한 post의 feedback 조회 및 각 feedback에 대한 feedbackReply 조회 -> 이후 ResFeedbackDto로 매핑
        List<ResFeedbackDto> resFeedbackDtos = memberUtils.feedbackList(reply.getPost().getPostId(), myMember);


        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, resFeedbackDtos);
        return ResponseEntity.ok(response);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 대댓글을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reference/feedback/{feedback_id}/reply/{reply_id}") // 피드백 대댓글 삭제
    @Operation(summary = "피드백 대댓글 삭제 Test Completed", description = "작성한 피드백 대댓글을 삭제합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> deleteFeedbackReply(@PathVariable("reply_id") Long replyId,
                                                                                  @AuthenticationPrincipal MemberDetails memberDetails) {
        log.info("EndPoint Delete /reference/feedback/{feedback_id}/reply/{reply_id}");

       FeedbackReply reply = feedbackReplyService.findOne(replyId);

        Long myMemberId = memberDetails.getMemberId();
        if (!Objects.equals(reply.getMember().getMemberId(),myMemberId)) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
        }

        feedbackReplyService.deleteFeedbackReply(replyId);

        Member myMember = memberService.findOne(myMemberId);
        // 조회한 post의 feedback 조회 및 각 feedback에 대한 feedbackReply 조회 -> 이후 ResFeedbackDto로 매핑
        List<ResFeedbackDto> resFeedbackDtos = memberUtils.feedbackList(reply.getPost().getPostId(), myMember);

        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, resFeedbackDtos);
        return ResponseEntity.ok(response);
    }


}
