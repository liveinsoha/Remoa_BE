package Remoa.BE.Web.Post.Controller;

import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.Comment.Domain.CommentReply;
import Remoa.BE.Web.Comment.Dto.Res.ResCommentReplyDto;
import Remoa.BE.Web.Comment.Service.CommentReplyService;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Web.Member.MemberUtils;
import Remoa.BE.Web.Member.Service.FollowService;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.Web.Post.Domain.Post;
import Remoa.BE.Web.Post.Dto.Response.ReceivedFeedbackResponse;
import Remoa.BE.Web.Comment.Dto.Res.ResCommentDto;
import Remoa.BE.Web.Post.Dto.Response.ResReceivedCommentDto;
import Remoa.BE.Web.Post.Dto.Response.ResReplyDto;
import Remoa.BE.Web.Comment.Service.CommentService;
import Remoa.BE.Web.Post.Service.MyPostService;
import Remoa.BE.config.auth.MemberDetails;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import Remoa.BE.exception.response.BaseResponse;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MemberInfo;
import Remoa.BE.utill.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Remoa.BE.config.DbInit.categoryList;

@Tag(name = "받은 피드백 기능", description = "받은 피드백 기능 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MyFeedbackAndCommentController {

    private final MemberService memberService;
    private final MyPostService myPostService;
    private final CommentService commentService;
    private final FollowService followService;
    private final CommentReplyService commentReplyService;
    private final MemberUtils memberUtils;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 받은 피드백과 코멘트를 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "페이지 번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/feedback")
    @Operation(summary = "내가 받은 피드백 조회", description = "내가 받은 최신 피드백들을 조회합니다.")
    public ResponseEntity<BaseResponse<ReceivedFeedbackResponse>> receivedFeedback(HttpServletRequest request,
                                                                                   @RequestParam(required = false, defaultValue = "all") String category,
                                                                                   @RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber,
                                                                                   @AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);

        pageNumber -= 1;
        if (pageNumber < 0) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

//            Page<Post> posts = myPostService.getNewestThreePosts(pageNumber, myMember);
        Page<Post> posts;
        if (categoryList.contains(category)) {
            posts = myPostService.getNewestThreePostsSortCategory(pageNumber, myMember, category);

        } else {
            posts = myPostService.getNewestThreePosts(pageNumber, myMember);
        }


        if ((posts.getContent().isEmpty()) && (posts.getTotalElements() > 0)) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        List<ResReceivedCommentDto> res = new ArrayList<>();

        for (Post post : posts) { //조회한 post
            Map<String, ResCommentDto> commentInfo = new HashMap<>();

            List<Comment> commnets = commentService.getRecentThreeComments(post);

            int commentNumber = 1;
            for (Comment parentComment : commnets) { //조회한 post의 parent comment
                List<CommentReply> commentReplies = commentReplyService.findCommentReplies(parentComment);


                List<ResCommentReplyDto> replies = new ArrayList<>();
                for (CommentReply reply : commentReplies) {

                    replies.add(new ResCommentReplyDto(
                            reply.getCommentReplyId(),
                            new ResMemberInfoDto(reply.getMember(),
                                    memberUtils.isMymMemberFollowMember(myMember, reply.getMember())),
                            reply.getContent(),
                            reply.getLikeCount(),
                            commentReplyService.findCommentReplyLike(myMember, reply).isPresent(),
                            reply.getCommentRepliedTime()));
                }

                commentInfo.put("comment_" + commentNumber, new ResCommentDto(
                        parentComment.getCommentId(),
                        new ResMemberInfoDto(parentComment.getMember().getMemberId(),
                                parentComment.getMember().getNickname(),
                                parentComment.getMember().getProfileImage(),
                                followService.isMyMemberFollowMember(myMember, parentComment.getMember())),
                        parentComment.getContent(),
                        parentComment.getLikeCount(),
                        commentService.findCommentLike(myMember, parentComment).isPresent(),
                        parentComment.getCommentedTime(),
                        null));
                commentNumber++;
            }

            ResReceivedCommentDto map = ResReceivedCommentDto.builder()
                    .title(post.getTitle())
                    .thumbnail(post.getThumbnail().getStoreFileUrl())
                    .postId(post.getPostId())
                    .commentInfo(commentInfo)
                    .build();

            res.add(map);
        }

        ReceivedFeedbackResponse responseDto = ReceivedFeedbackResponse.builder()
                .posts(res)
                .totalPages(posts.getTotalPages())
                .totalOfAllComments(posts.getTotalElements())
                .totalOfPageElements(posts.getNumberOfElements())
                .build();


        BaseResponse<ReceivedFeedbackResponse> response = new BaseResponse<>(CustomMessage.OK, responseDto);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, result);
    }

}
