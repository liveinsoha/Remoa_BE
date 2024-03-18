package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.Feedback;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Dto.Response.ResCommentDto;
import Remoa.BE.Post.Dto.Response.ResFeedbackDto;
import Remoa.BE.Post.Dto.Response.ResReferenceViewerDto;
import Remoa.BE.Post.Dto.Response.ResReplyDto;
import Remoa.BE.Post.Service.CommentService;
import Remoa.BE.Post.Service.FeedbackService;
import Remoa.BE.Post.Service.PostService;
import Remoa.BE.exception.CustomMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.MemberInfo.authorized;
import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ViewerController {

    private final CommentService commentService;
    private final PostService postService;
    private final FeedbackService feedbackService;
    private final MemberService memberService;
    private final FollowService followService;

    @GetMapping("/reference/{reference_id}")
    @Operation(summary = "레퍼런스 조회", description = "특정 레퍼런스의 상세 정보를 조회합니다.")
    public ResponseEntity<Object> referenceViewer(HttpServletRequest request,
                                                  @PathVariable("reference_id") Long referenceId) {

        Long myMemberId = null;
        Member myMember = null;
        if (authorized(request)) {
            myMemberId = getMemberId();
            myMember = memberService.findOne(myMemberId);
        }
        final Long finalMyMemberId = myMemberId;
        final Member finalMyMember = myMember;

        // query parameter로 넘어온 id값의 post 조회
        Post post = postService.findOneViewPlus(referenceId);

        // 조회한 post의 comment(대댓글 제외) 조회 및 CommentDto로 매핑 -> 이후 주석에서 대댓글 comment 조회
        List<ResCommentDto> comments = commentList(post, myMember);

        // 조회한 post의 feedback(대댓글 제외) 조회 및 FeedbackDto로 매핑 -> 이후 주석에서 대댓글 feedback 조회
        List<ResFeedbackDto> feedbacks = feedbackList(post, myMember);

        List<String> fileNames = post.getUploadFiles() != null ?
                post.getUploadFiles().stream().map(UploadFile::getStoreFileUrl).collect(Collectors.toList()) : null;

        // 위에 생성한 CommentDto, FeedbackDto를 이용해 ReferenceViewerDto 매핑.
        ResReferenceViewerDto result = ResReferenceViewerDto.builder()
                .postId(post.getPostId())
                .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                        post.getMember().getNickname(),
                        post.getMember().getProfileImage(),
                        isMymMemberFollowMember(finalMyMember, post.getMember())))
                .thumbnail(post.getThumbnail().getStoreFileUrl())
                .contestName(post.getContestName())
                .contestAwardType(post.getContestAwardType())
                .category(post.getCategory().getName())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .isLiked(isLikedPost(myMember, post))
                .scrapCount(post.getScrapCount())
                .isScraped(isScrapedPost(myMember, post))
                .postingTime(post.getPostingTime().toString())
                .views(post.getViews())
                .youtubeLink(post.getYoutubeLink())
                .pageCount(post.getPageCount())
                .fileNames(fileNames)
                .comments(comments)
                .feedbacks(feedbacks)
                .build();

        return successResponse(CustomMessage.OK, result);
    }

    private Boolean isMymMemberFollowMember(Member myMember, Member member) {
        return myMember != null ? followService.isMyMemberFollowMember(myMember, member) : null;
    }

    private Boolean isLikedPost(Member myMember, Post post) {
        return myMember != null && postService.isThisPostLiked(myMember, post);
    }

    private Boolean isScrapedPost(Member myMember, Post post) {
        return myMember != null && postService.isThisPostScraped(myMember, post);
    }

    private Boolean isLikedComment(Long myMemberId, Comment comment) {
        return myMemberId != null && commentService.findCommentLike(myMemberId, comment.getCommentId()).isPresent();
    }

    private Boolean isLikedFeedback(Long myMemberId, Feedback feedback) {
        return myMemberId != null && feedbackService.findFeedbackLike(myMemberId, feedback.getFeedbackId()).isPresent();
    }

    private List<ResFeedbackDto> feedbackList(Post post, Member myMember) {
        return feedbackService.findAllFeedbacksOfPost(post).stream()
                .filter(feedback -> feedback.getParentFeedback() == null)
                .map(feedback -> ResFeedbackDto.builder()
                        .feedbackId(feedback.getFeedbackId())
                        .isLiked(isLikedFeedback(myMember.getMemberId(), feedback))
                        .member(new ResMemberInfoDto(feedback.getMember().getMemberId(),
                                feedback.getMember().getNickname(),
                                feedback.getMember().getProfileImage(),
                                isMymMemberFollowMember(myMember, post.getMember())))
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
                                                isMymMemberFollowMember(myMember, post.getMember())))
                                        .content(reply.getFeedback())
                                        .likeCount(reply.getFeedbackLikeCount())
                                        .isLiked(isLikedFeedback(myMember.getMemberId(), reply))
                                        .repliedTime(reply.getFeedbackTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());
    }

    private List<ResCommentDto> commentList(Post post, Member myMember) {
        return commentService.findAllCommentsOfPost(post).stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> ResCommentDto.builder()
                        .commentId(comment.getCommentId())
                        .isLiked(isLikedComment(myMember.getMemberId(), comment))
                        .member(new ResMemberInfoDto(comment.getMember().getMemberId(),
                                comment.getMember().getNickname(),
                                comment.getMember().getProfileImage(),
                                isMymMemberFollowMember(myMember, post.getMember())))
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
                                                isMymMemberFollowMember(myMember, post.getMember())))
                                        .content(reply.getComment())
                                        .likeCount(reply.getCommentLikeCount())
                                        .isLiked(isLikedComment(myMember.getMemberId(), reply))
                                        .repliedTime(reply.getCommentedTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());
    }
}
