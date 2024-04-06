package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Feedback;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Request.ReqFeedbackDto;
import Remoa.BE.Post.Dto.Response.ResFeedbackDto;
import Remoa.BE.Post.Dto.Response.ResLikeFeedbackDto;
import Remoa.BE.Post.Dto.Response.ResReplyDto;
import Remoa.BE.Post.Service.FeedbackService;
import Remoa.BE.Post.Service.PostService;
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
import java.util.stream.Collectors;

@Tag(name = "피드백 기능", description = "피드백 기능 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MemberService memberService;
    private final PostService postService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백을 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "400", description = "게시물 페이지 번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/{reference_id}/{page_number}") // 레퍼런스에 피드백 등록
    @Operation(summary = "피드백 등록", description = "특정 게시물 페이지에 피드백을 등록합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> registerFeedback(@RequestBody ReqFeedbackDto req,
                                                                               @PathVariable("reference_id") Long postId,
                                                                               @PathVariable("page_number") Integer pageNumber,
                                                                               @AuthenticationPrincipal MemberDetails memberDetails) {
        String myFeedback = req.getFeedback();

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        Post post = postService.findOne(postId);
        // 피드백을 등록하려는 게시물의 페이지 수가 없을 경우 예외 처리
        if (post.getPageCount() < pageNumber || pageNumber < 1) {
            throw new BaseException(CustomMessage.BAD_PAGE_NUM);
            //return errorResponse(CustomMessage.BAD_PAGE_NUM);
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
        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, feedbacks);
        return ResponseEntity.ok(response);
        //  return successResponse(CustomMessage.OK,feedbacks);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 대댓글을 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/{reference_id}/feedback/{feedback_id}") // 레퍼런스에 피드백 대댓글 등록
    @Operation(summary = "피드백 대댓글 등록", description = "특정 피드백에 대댓글을 등록합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> registerFeedbackReply(@RequestBody ReqFeedbackDto req,
                                                                                    @PathVariable("reference_id") Long postId,
                                                                                    @PathVariable("feedback_id") Long feedbackId,
                                                                                    @AuthenticationPrincipal MemberDetails memberDetails) {
        String myFeedback = req.getFeedback();
        Long memberId = memberDetails.getMemberId();
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
        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, feedbacks);
        return ResponseEntity.ok(response);
        //return successResponse(CustomMessage.OK, feedbacks);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백을 성공적으로 수정했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reference/feedback/{feedback_id}") // 피드백 수정
    @Operation(summary = "피드백 수정", description = "작성한 피드백을 수정합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> modifyFeedback(@RequestBody ReqFeedbackDto req,
                                                                             @PathVariable("feedback_id") Long feedbackId,
                                                                             @AuthenticationPrincipal MemberDetails memberDetails) {
        String myFeedback = req.getFeedback();

        feedbackService.modifyFeedback(myFeedback, feedbackId);
        Feedback f = feedbackService.findOne(feedbackId);

        if (!Objects.equals(f.getMember().getMemberId(), memberDetails.getMemberId())) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
            //return errorResponse(CustomMessage.CAN_NOT_ACCESS);
        }

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

        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, feedbacks);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, feedbacks);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reference/feedback/{feedback_id}")
    @Operation(summary = "피드백 삭제", description = "작성한 피드백을 삭제합니다.")
    public ResponseEntity<BaseResponse<List<ResFeedbackDto>>> deleteFeedback(@PathVariable("feedback_id") Long feedbackId,
                                                                             @AuthenticationPrincipal MemberDetails memberDetails) {

        feedbackService.deleteFeedback(feedbackId);
        Feedback f = feedbackService.findOne(feedbackId);

        if (!Objects.equals(f.getMember().getMemberId(), memberDetails.getMemberId())) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
            //   return errorResponse(CustomMessage.CAN_NOT_ACCESS);
        }

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
        BaseResponse<List<ResFeedbackDto>> response = new BaseResponse<>(CustomMessage.OK, feedbacks);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, feedbacks);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백에 좋아요를 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/feedback/{feedback_id}/like") // 피드백 좋아요
    @Operation(summary = "피드백 좋아요", description = "피드백에 좋아요를 누릅니다.")
    public ResponseEntity<BaseResponse<ResLikeFeedbackDto>> likeFeedback(@PathVariable("feedback_id") Long feedbackId,
                                                                         @AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        feedbackService.likeFeedback(memberId, myMember, feedbackId);
        int count = feedbackService.feedbackLikeCount(feedbackId);
        ResLikeFeedbackDto dto = new ResLikeFeedbackDto(count);
        BaseResponse<ResLikeFeedbackDto> response = new BaseResponse<>(CustomMessage.OK, dto);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, map);
    }
}