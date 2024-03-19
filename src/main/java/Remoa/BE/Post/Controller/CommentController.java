package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Request.ReqCommentDto;
import Remoa.BE.Post.Dto.Response.ResCommentDto;
import Remoa.BE.Post.Dto.Response.ResReplyDto;
import Remoa.BE.Post.Service.CommentService;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import io.swagger.v3.oas.annotations.Operation;
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
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final PostService postService;
    private final FollowService followService;

    //댓글 작성
    @PostMapping("/reference/{reference_id}/comment")
    @Operation(summary = "게시물 댓글 작성", description = "게시물에 댓글을 작성합니다.")
    public ResponseEntity<Object> registerComment(@RequestBody ReqCommentDto req, @PathVariable("reference_id") Long postId, HttpServletRequest request){
        String myComment = req.getComment();
        if(authorized(request)){
            Long memberId = getMemberId();
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
            return  successResponse(CustomMessage.OK,comments);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    //대댓글 작성
    @PostMapping("/reference/{reference_id}/comment/{comment_id}")
    @Operation(summary = "대댓글 작성", description = "특정 댓글에 대댓글을 작성합니다.")
    public ResponseEntity<Object> registerCommentReply(@RequestBody ReqCommentDto req, @PathVariable("reference_id") Long postId, @PathVariable("comment_id") Long commentId, HttpServletRequest request){
        String myComment = req.getComment();
        if(authorized(request)){

            Long memberId = getMemberId();
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
            return  successResponse(CustomMessage.OK,comments);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }
    @PutMapping("/reference/comment/{comment_id}")
    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다.")
    public ResponseEntity<Object> modifyComment(@RequestBody ReqCommentDto req, @PathVariable("comment_id") Long commentId, HttpServletRequest request){
        String myComment = req.getComment();

        if(authorized(request)){
            commentService.modifyComment(myComment, commentId);
            Comment c = commentService.findOne(commentId);

            if(c.getMember().getMemberId() != getMemberId()) {
                return errorResponse(CustomMessage.CAN_NOT_ACCESS);
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

            return  successResponse(CustomMessage.OK,comments);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @DeleteMapping("/reference/comment/{comment_id}")
    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    public ResponseEntity<Object> deleteComment(@PathVariable("comment_id") Long commentId, HttpServletRequest request){
        if(authorized(request)){
            commentService.deleteComment(commentId);
            Comment c = commentService.findOne(commentId);

            if(c.getMember().getMemberId() != getMemberId()) {
                return errorResponse(CustomMessage.CAN_NOT_ACCESS);
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
            return  successResponse(CustomMessage.OK,comments);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }

    @PostMapping("/comment/{comment_id}/like") // 댓글 좋아요
    @Operation(summary = "댓글 좋아요", description = "댓글에 좋아요를 누릅니다.")
    public ResponseEntity<Object> likeComment(@PathVariable("comment_id") Long commentId, HttpServletRequest request){
        if(authorized(request)){
            Long memberId = getMemberId();
            commentService.likeComment(memberId, commentId);
            int count = commentService.commentLikeCount(commentId);
            Map<String, Integer> map = Collections.singletonMap("LikeCount", count);
            return successResponse(CustomMessage.OK,map);
        }
        return errorResponse(CustomMessage.UNAUTHORIZED);
    }
}