package Remoa.BE.Post.Controller;

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
        List<ResCommentDto> comments = commentService.findAllCommentsOfPost(post).stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> ResCommentDto.builder()
                        .commentId(comment.getCommentId())
                        .isLiked(finalMyMemberId != null && commentService.findCommentLike(finalMyMemberId, comment.getCommentId()).isPresent())
                        .member(new ResMemberInfoDto(comment.getMember().getMemberId(),
                                comment.getMember().getNickname(),
                                comment.getMember().getProfileImage(),
                                finalMyMember != null ? followService.isMyMemberFollowMember(finalMyMember, post.getMember()) : null))
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
                                                finalMyMember != null ? followService.isMyMemberFollowMember(finalMyMember, post.getMember()) : null))
                                        .content(reply.getComment())
                                        .likeCount(reply.getCommentLikeCount())
                                        .isLiked(finalMyMemberId != null && commentService.findCommentLike(finalMyMemberId, reply.getCommentId()).isPresent())
                                        .repliedTime(reply.getCommentedTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());

        // 조회한 post의 feedback(대댓글 제외) 조회 및 FeedbackDto로 매핑 -> 이후 주석에서 대댓글 feedback 조회
        List<ResFeedbackDto> feedbacks = feedbackService.findAllFeedbacksOfPost(post).stream()
                .filter(feedback -> feedback.getParentFeedback() == null)
                .map(feedback -> ResFeedbackDto.builder()
                        .feedbackId(feedback.getFeedbackId())
                        .isLiked(finalMyMemberId != null && feedbackService.findFeedbackLike(finalMyMemberId, feedback.getFeedbackId()).isPresent())
                        .member(new ResMemberInfoDto(feedback.getMember().getMemberId(),
                                feedback.getMember().getNickname(),
                                feedback.getMember().getProfileImage(),
                                finalMyMember != null ? followService.isMyMemberFollowMember(finalMyMember, post.getMember()) : null))
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
                                                finalMyMember != null ? followService.isMyMemberFollowMember(finalMyMember, post.getMember()) : null))
                                        .content(reply.getFeedback())
                                        .likeCount(reply.getFeedbackLikeCount())
                                        .isLiked(finalMyMemberId != null && feedbackService.findFeedbackLike(finalMyMemberId, reply.getFeedbackId()).isPresent())
                                        .repliedTime(reply.getFeedbackTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());

        List<String> fileNames = post.getUploadFiles() != null ?
                post.getUploadFiles().stream().map(UploadFile::getStoreFileUrl).collect(Collectors.toList()) : null;

        // 위에 생성한 CommentDto, FeedbackDto를 이용해 ReferenceViewerDto 매핑.
        ResReferenceViewerDto result = ResReferenceViewerDto.builder()
                .postId(post.getPostId())
                .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                        post.getMember().getNickname(),
                        post.getMember().getProfileImage(),
                        finalMyMember != null ? followService.isMyMemberFollowMember(finalMyMember, post.getMember()) : null))
                .thumbnail(post.getThumbnail().getStoreFileUrl())
                .contestName(post.getContestName())
                .contestAwardType(post.getContestAwardType())
                .category(post.getCategory().getName())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .isLiked(finalMyMemberId != null && postService.isThisPostLiked(myMember))
                .scrapCount(post.getScrapCount())
                .isScraped(finalMyMemberId != null && postService.isThisPostScraped(myMember))
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
}