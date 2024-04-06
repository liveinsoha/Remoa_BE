package Remoa.BE.Comment.Controller;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Request.ReqCommentDto;
import Remoa.BE.Post.Dto.Response.ResCommentDto;
import Remoa.BE.Post.Dto.Response.ResReplyDto;
import Remoa.BE.Comment.Service.CommentService;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static Remoa.BE.exception.CustomBody.successResponse;

@Tag(name = "댓글 기능", description = "댓글 기능 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final PostService postService;
    private final FollowService followService;

    //댓글 작성

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글을 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/{reference_id}/comment")
    @Operation(summary = "게시물 댓글 작성", description = "게시물에 댓글을 작성합니다.")
    public ResponseEntity<BaseResponse<List<ResCommentDto>>> registerComment(@RequestBody ReqCommentDto req,
                                                                             @PathVariable("reference_id") Long postId,
                                                                             @AuthenticationPrincipal MemberDetails memberDetails) {
        String myComment = req.getComment();
        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        Post post = postService.findOne(postId);
        commentService.registerComment(myMember, myComment, postId, null);

        List<ResCommentDto> comments = commentService.findAllCommentsOfPost(post).stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> ResCommentDto.builder()
                        .commentId(comment.getCommentId())
                        .member(new ResMemberInfoDto(comment.getMember().getMemberId(),
                                comment.getMember().getNickname(),
                                comment.getMember().getProfileImage(),
                                null))
                        .comment(comment.getComment())
                        .likeCount(comment.getCommentLikeCount())
                        .commentedTime(comment.getCommentedTime())
                        //아래부터 대댓글 comment 조회 및 dto 매핑
                        .replies(commentService.getParentCommentsReply(comment).stream()
                                .map(reply -> ResReplyDto.builder()
                                        .replyId(reply.getCommentId())
                                        .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                reply.getMember().getNickname(),
                                                reply.getMember().getProfileImage(),
                                                null))
                                        .content(reply.getComment())
                                        .likeCount(reply.getCommentLikeCount())
                                        .repliedTime(reply.getCommentedTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());

        BaseResponse<List<ResCommentDto>> response = new BaseResponse<>(CustomMessage.OK, comments);
        return ResponseEntity.ok(response);
        //   return  successResponse(CustomMessage.OK,comments);
    }

    //대댓글 작성

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글을 성공적으로 등록했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reference/{reference_id}/comment/{comment_id}")
    @Operation(summary = "대댓글 작성", description = "특정 댓글에 대댓글을 작성합니다.")
    public ResponseEntity<BaseResponse<List<ResCommentDto>>> registerCommentReply(@RequestBody ReqCommentDto req,
                                                                                  @PathVariable("reference_id") Long postId,
                                                                                  @PathVariable("comment_id") Long commentId,
                                                                                  @AuthenticationPrincipal MemberDetails memberDetails) {
        String myComment = req.getComment();
        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);
        Post post = postService.findOne(postId);

        commentService.registerComment(myMember, myComment, postId, commentId);
        List<ResCommentDto> comments = commentService.findAllCommentsOfPost(post).stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> ResCommentDto.builder()
                        .commentId(comment.getCommentId())
                        .member(new ResMemberInfoDto(comment.getMember().getMemberId(),
                                comment.getMember().getNickname(),
                                comment.getMember().getProfileImage(),
                                null))
                        .comment(comment.getComment())
                        .likeCount(comment.getCommentLikeCount())
                        .commentedTime(comment.getCommentedTime())
                        //아래부터 대댓글 comment 조회 및 dto 매핑
                        .replies(commentService.getParentCommentsReply(comment).stream()
                                .map(reply -> ResReplyDto.builder()
                                        .replyId(reply.getCommentId())
                                        .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                reply.getMember().getNickname(),
                                                reply.getMember().getProfileImage(),
                                                null))
                                        .content(reply.getComment())
                                        .likeCount(reply.getCommentLikeCount())
                                        .repliedTime(reply.getCommentedTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());
        BaseResponse<List<ResCommentDto>> response = new BaseResponse<>(CustomMessage.OK, comments);
        return ResponseEntity.ok(response);
        //return successResponse(CustomMessage.OK, comments);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글을 성공적으로 수정했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reference/comment/{comment_id}")
    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다.")
    public ResponseEntity<BaseResponse<List<ResCommentDto>>> modifyComment(@RequestBody ReqCommentDto req,
                                                                           @PathVariable("comment_id") Long commentId,
                                                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        String myComment = req.getComment();

        commentService.modifyComment(myComment, commentId);
        Comment c = commentService.findOne(commentId);


        if (!Objects.equals(c.getMember().getMemberId(), memberDetails.getMemberId())) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
            //return errorResponse(CustomMessage.CAN_NOT_ACCESS);
        }

        List<ResCommentDto> comments = commentService.findAllCommentsOfPost(c.getPost()).stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> ResCommentDto.builder()
                        .commentId(comment.getCommentId())
                        .member(new ResMemberInfoDto(comment.getMember().getMemberId(),
                                comment.getMember().getNickname(),
                                comment.getMember().getProfileImage(),
                                null))
                        .comment(comment.getComment())
                        .likeCount(comment.getCommentLikeCount())
                        .commentedTime(comment.getCommentedTime())
                        //아래부터 대댓글 comment 조회 및 dto 매핑
                        .replies(commentService.getParentCommentsReply(comment).stream()
                                .map(reply -> ResReplyDto.builder()
                                        .replyId(reply.getCommentId())
                                        .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                reply.getMember().getNickname(),
                                                reply.getMember().getProfileImage(),
                                                null))
                                        .content(reply.getComment())
                                        .likeCount(reply.getCommentLikeCount())
                                        .repliedTime(reply.getCommentedTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());

        BaseResponse<List<ResCommentDto>> response = new BaseResponse<>(CustomMessage.OK, comments);
        return ResponseEntity.ok(response);
        //return successResponse(CustomMessage.OK, comments);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reference/comment/{comment_id}")
    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    public ResponseEntity<BaseResponse<List<ResCommentDto>>> deleteComment(@PathVariable("comment_id") Long commentId,
                                                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        commentService.deleteComment(commentId);
        Comment c = commentService.findOne(commentId);


        if (!Objects.equals(c.getMember().getMemberId(), memberDetails.getMemberId())) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
            //    return errorResponse(CustomMessage.CAN_NOT_ACCESS);
        }

        List<ResCommentDto> comments = commentService.findAllCommentsOfPost(c.getPost()).stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> ResCommentDto.builder()
                        .commentId(comment.getCommentId())
                        .member(new ResMemberInfoDto(comment.getMember().getMemberId(),
                                comment.getMember().getNickname(),
                                comment.getMember().getProfileImage(),
                                null))
                        .comment(comment.getComment())
                        .likeCount(comment.getCommentLikeCount())
                        .commentedTime(comment.getCommentedTime())
                        //아래부터 대댓글 comment 조회 및 dto 매핑
                        .replies(commentService.getParentCommentsReply(comment).stream()
                                .map(reply -> ResReplyDto.builder()
                                        .replyId(reply.getCommentId())
                                        .member(new ResMemberInfoDto(reply.getMember().getMemberId(),
                                                reply.getMember().getNickname(),
                                                reply.getMember().getProfileImage(),
                                                null))
                                        .content(reply.getComment())
                                        .likeCount(reply.getCommentLikeCount())
                                        .repliedTime(reply.getCommentedTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());
        BaseResponse<List<ResCommentDto>> response = new BaseResponse<>(CustomMessage.OK, comments);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, comments);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글에 성공적으로 좋아요를 눌렀습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/comment/{comment_id}/like") // 댓글 좋아요
    @Operation(summary = "댓글 좋아요", description = "댓글에 좋아요를 누릅니다.")
    public ResponseEntity<Object> likeComment(@PathVariable("comment_id") Long commentId,
                                              @AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        commentService.likeComment(memberId, commentId);
        int count = commentService.commentLikeCount(commentId);
        Map<String, Integer> map = Collections.singletonMap("LikeCount", count);
        return successResponse(CustomMessage.OK, map);
    }

}