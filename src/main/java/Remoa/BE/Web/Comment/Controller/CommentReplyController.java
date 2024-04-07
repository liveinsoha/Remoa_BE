package Remoa.BE.Web.Comment.Controller;

import Remoa.BE.Web.Comment.Domain.CommentReply;
import Remoa.BE.Web.Comment.Dto.Req.ReqCommentReplyDto;
import Remoa.BE.Web.Comment.Dto.Res.ResCommentDto;
import Remoa.BE.Web.Comment.Service.CommentReplyService;
import Remoa.BE.Web.Comment.Service.CommentService;
import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.MemberUtils;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.Web.Post.Domain.Post;
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

@Tag(name = "레퍼런스 대댓글 기능", description = "레퍼런스 댓글 기능 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentReplyController {

    private final MemberService memberService;
    private final CommentReplyService commentReplyService;
    private final MemberUtils memberUtils;

    //대댓글 작성
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글을 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/{reference_id}/comment/{comment_id}")
    @Operation(summary = "레퍼런스 대댓글 작성", description = "특정 댓글에 대댓글을 작성합니다.")
    public ResponseEntity<BaseResponse<List<ResCommentDto>>> registerCommentReply(@RequestBody ReqCommentReplyDto req,
                                                                                  @PathVariable("reference_id") Long postId,
                                                                                  @PathVariable("comment_id") Long commentId,
                                                                                  @AuthenticationPrincipal MemberDetails memberDetails) {
        String content = req.getCommentReplyContent();
        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);

        commentReplyService.registerCommentReply(myMember, content, postId, commentId);


        // 조회한 post의 comment 조회 및 각 comment에 대한 commentReply 조회 -> 이후 ResCommentDto로 매핑
        List<ResCommentDto> resCommentDtos = memberUtils.commentList(postId, myMember);

        BaseResponse<List<ResCommentDto>> response = new BaseResponse<>(CustomMessage.OK, resCommentDtos);
        return ResponseEntity.ok(response);
    }


    // 대댓글 수정
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글을 성공적으로 수정했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reference/comment/{comment_id}/reply/{reply_id}")
    @Operation(summary = "레퍼런스 대댓글 수정", description = "작성한 레퍼런스 대댓글을 수정합니다.")
    public ResponseEntity<BaseResponse<List<ResCommentDto>>> modifyCommentReply(@RequestBody ReqCommentReplyDto req,
                                                                                @PathVariable("comment_id") Long commentId,
                                                                                @PathVariable("reply_id") Long replyId,
                                                                                @AuthenticationPrincipal MemberDetails memberDetails) {
        String content = req.getCommentReplyContent();
        CommentReply reply = commentReplyService.findOne(replyId);

        if (!Objects.equals(reply.getMember().getMemberId(), memberDetails.getMemberId())) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
        }
        commentReplyService.modifyCommentReply(content, replyId);

        Member myMember = memberService.findOne(memberDetails.getMemberId());
        // 조회한 post의 comment 조회 및 각 comment에 대한 commentReply 조회 -> 이후 ResCommentDto로 매핑
        List<ResCommentDto> resCommentDtos = memberUtils.commentList(reply.getPost().getPostId(), myMember);

        BaseResponse<List<ResCommentDto>> response = new BaseResponse<>(CustomMessage.OK, resCommentDtos);
        return ResponseEntity.ok(response);
    }

    // 대댓글 삭제
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레퍼런스 대댓글을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reference/comment/{comment_id}/reply/{reply_id}")
    @Operation(summary = "레퍼런스 대댓글 삭제", description = "작성한 레퍼런스 대댓글을 삭제합니다.")
    public ResponseEntity<BaseResponse<List<ResCommentDto>>> deleteCommentReply(@PathVariable("reply_id") Long replyId,
                                                                                @AuthenticationPrincipal MemberDetails memberDetails) {
        CommentReply reply = commentReplyService.findOne(replyId);

        if (!Objects.equals(reply.getMember().getMemberId(), memberDetails.getMemberId())) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
        }

        commentReplyService.deleteCommentReply(replyId);

        Member myMember = memberService.findOne(memberDetails.getMemberId());
        // 조회한 post의 comment 조회 및 각 comment에 대한 commentReply 조회 -> 이후 ResCommentDto로 매핑
        List<ResCommentDto> resCommentDtos = memberUtils.commentList(reply.getPost().getPostId(), myMember);

        BaseResponse<List<ResCommentDto>> response = new BaseResponse<>(CustomMessage.OK, resCommentDtos);
        return ResponseEntity.ok(response);
    }
}
