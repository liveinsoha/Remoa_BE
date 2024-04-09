package Remoa.BE.Web.Feedback.Controller;

import Remoa.BE.Web.Feedback.Domain.Feedback;
import Remoa.BE.Web.Feedback.Dto.ResFeedbackLikeDto;
import Remoa.BE.Web.Feedback.Service.FeedbackReplyService;
import Remoa.BE.Web.Feedback.Service.FeedbackService;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.MemberUtils;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.Web.Post.Domain.Post;
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

@Tag(name = "레퍼런스 피드백 기능 Test completed", description = "레퍼런스 피드백 기능 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MemberService memberService;
    private final PostService postService;
    private final FeedbackReplyService feedbackReplyService;
    private final MemberUtils memberUtils;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백을 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "400", description = "게시물 페이지 번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/{reference_id}/{page_number}") // 레퍼런스에 피드백 등록
    @Operation(summary = "피드백 등록 Test completed", description = "특정 게시물 페이지에 피드백을 등록합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> registerFeedback(@RequestBody ReqFeedbackDto req,
                                                                               @PathVariable("reference_id") Long postId,
                                                                               @PathVariable("page_number") Integer pageNumber,
                                                                               @AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        Post post = postService.findOne(postId);

        // 피드백을 등록하려는 게시물의 페이지 수가 없을 경우 예외 처리
        if (post.getPageCount() < pageNumber || pageNumber < 1) {
            throw new BaseException(CustomMessage.BAD_PAGE_NUM);
        }

        String content = req.getContent();
        feedbackService.registerFeedback(myMember, content, postId, pageNumber);

        // 조회한 post의 feedback 조회 및 각 feedback에 대한 feedbackReply 조회 -> 이후 ResFeedbackDto로 매핑
        List<ResFeedbackDto> resFeedbackDtos = memberUtils.feedbackList(postId, myMember);

        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, resFeedbackDtos);
        return ResponseEntity.ok(response);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백을 성공적으로 수정했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reference/feedback/{feedback_id}") // 피드백 수정
    @Operation(summary = "피드백 수정 Test completed", description = "작성한 피드백을 수정합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> modifyFeedback(@RequestBody ReqFeedbackDto req,
                                                                             @PathVariable("feedback_id") Long feedbackId,
                                                                             @AuthenticationPrincipal MemberDetails memberDetails) {

        Feedback f = feedbackService.findOne(feedbackId);
        Long memberId = memberDetails.getMemberId();
        if (!Objects.equals(f.getMember().getMemberId(), memberId)) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);

        }
        String myFeedback = req.getContent();
        feedbackService.modifyFeedback(myFeedback, feedbackId);

        Member myMember = memberService.findOne(memberId);
        // 조회한 post의 feedback 조회 및 각 feedback에 대한 feedbackReply 조회 -> 이후 ResFeedbackDto로 매핑
        List<ResFeedbackDto> resFeedbackDtos = memberUtils.feedbackList(f.getPost().getPostId(), myMember);

        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, resFeedbackDtos);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, feedbacks);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reference/feedback/{feedback_id}")
    @Operation(summary = "피드백 삭제 Test Completed", description = "작성한 피드백을 삭제합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> deleteFeedback(@PathVariable("feedback_id") Long feedbackId,
                                                                             @AuthenticationPrincipal MemberDetails memberDetails) {

        Feedback f = feedbackService.findOne(feedbackId);
        Long memberId = memberDetails.getMemberId();
        if (!Objects.equals(f.getMember().getMemberId(), memberId)) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);

        }
        feedbackService.deleteFeedback(feedbackId);

        Member myMember = memberService.findOne(memberId);
        // 조회한 post의 feedback 조회 및 각 feedback에 대한 feedbackReply 조회 -> 이후 ResFeedbackDto로 매핑
        List<ResFeedbackDto> resFeedbackDtos = memberUtils.feedbackList(f.getPost().getPostId(), myMember);

        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, resFeedbackDtos);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, feedbacks);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백에 좋아요를 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/feedback/{feedback_id}/like") // 피드백 좋아요
    @Operation(summary = "피드백 좋아요 Test Completed", description = "피드백에 좋아요를 누릅니다.")
    public ResponseEntity<BaseResponse<ResFeedbackLikeDto>> likeFeedback(@PathVariable("feedback_id") Long feedbackId,
                                                                         @AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        feedbackService.likeFeedback(memberId, myMember, feedbackId);
        int count = feedbackService.feedbackLikeCount(feedbackId);
        ResFeedbackLikeDto dto = new ResFeedbackLikeDto(count);
        BaseResponse<ResFeedbackLikeDto> response = new BaseResponse<>(CustomMessage.OK, dto);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, map);
    }


}