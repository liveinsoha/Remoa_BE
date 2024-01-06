package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.*;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.CommentPagingRepository;
import Remoa.BE.Post.Repository.CommentLikeRepository;
import Remoa.BE.Post.Repository.CommentRepository;
import Remoa.BE.Post.Repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static Remoa.BE.Member.Domain.ContentType.COMMENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentLikeRepository commentLikeRepository;
    private final PostService postService;
    private final CommentPagingRepository commentPagingRepository;
    private final CommentFeedbackService commentFeedbackService;
    private final MemberService memberService;

    @Transactional
    public Long writeComment(Comment comment) {
        commentRepository.saveComment(comment);
        return comment.getCommentId();
    }

    public List<Comment> findAllCommentsOfPost(Post post) {
        return commentRepository.findByPost(post);
    }

    @Transactional
    public Long commentLikeAction(Comment comment, Member member) {
        CommentLike commentLike = CommentLike.createCommentLike(member, comment);
        commentRepository.saveCommentLike(commentLike);
        return commentLike.getCommentLikeId();
    }

    public Comment findOne(Long commentId) {
        Optional<Comment> comment = commentRepository.findOne(commentId);
        return comment.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));
    }

    public int commentLikeCount(Long commentId){
        Comment comment = findOne(commentId);
        return comment.getCommentLikeCount();
    }

    @Transactional
    public Long commentBookmarkAction(Comment comment, Member member) {
        CommentBookmark commentBookmark = CommentBookmark.createCommentBookmark(member, comment);
        commentRepository.saveCommentBookmark(commentBookmark);
        return commentBookmark.getCommentBookmarkId();
    }

    @Transactional
    public Comment registerComment(Member member, String comment, Long postId, Long commentId){

        Comment parentComment = null;

        if (commentId != null) {
            parentComment = commentRepository.findByCommentId(commentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));
        }

        Comment commentObj = new Comment();
//        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Post post = postService.findOne(postId);
        LocalDateTime time = LocalDateTime.now();

        commentObj.setPost(post);
        commentObj.setMember(member);
        commentObj.setParentComment(parentComment); //대댓글인 경우 원 댓글의 Feedback, 댓글인 경우 null
        commentObj.setComment(comment);
        commentObj.setCommentLikeCount(0);
        commentObj.setCommentedTime(time);
        commentRepository.saveComment(commentObj);

        if (parentComment == null) {
            commentFeedbackService.saveCommentFeedback(commentObj, null, COMMENT, member, post, time);
        }

        return commentObj;

    }

    @Transactional
    public void modifyComment(String comment, Long commentId){
        Comment commentObj = findOne(commentId);
        commentObj.setComment(comment);
        if (commentObj.getParentComment() == null) {
            commentFeedbackService.findComment(commentObj).getComment().setComment(comment);
        }
        commentRepository.updateComment(commentObj);
    }


    @Transactional
    public void deleteComment(Long commentId){
        Comment commentObj = findOne(commentId);
        if(commentObj.getParentComment() == null) {
            CommentFeedback commentOfCommentFeedback = commentFeedbackService.findComment(commentObj);
            commentOfCommentFeedback.setDeleted(true);
        }
        commentObj.setDeleted(true);
    }

    public List<Comment> getRecentThreeCommentsExceptReply(Post post) {
        PageRequest pageable = PageRequest.of(0, 3, Sort.by("commentedTime").descending());
        return commentPagingRepository.findByParentCommentIsNullAndPost(pageable, post).getContent();
    }

    public List<Comment> getParentCommentsReply(Comment parentComment) {
        return commentRepository.findRepliesOfParentComment(parentComment);
    }

    public Optional<CommentLike> findCommentLike(Long memberId, Long commentId) {
        return commentLikeRepository.findByMemberMemberIdAndCommentCommentId(memberId, commentId);
    }

    @Transactional
    public void likeComment(Long memberId, Long commentId){
        Comment commentObj = findOne(commentId);
        Integer commentLikeCount = commentObj.getCommentLikeCount();
        Member myMember = memberService.findOne(memberId);

        // CommentLike를 db에서 조회해보고 조회 결과가 null이면 like+=1, CommentLike 엔티티 생성
        // null이 아니면 like -= 1, 조회결과인 해당 CommentLike 엔티티 삭제
        Optional<CommentLike> commentLike = findCommentLike(memberId, commentId);
        if(commentLike.isEmpty()){
            commentObj.setCommentLikeCount(commentLikeCount + 1); // 좋아요 수 1 증가
            CommentLike commentLikeObj = CommentLike.createCommentLike(myMember, commentObj);
            commentLikeRepository.save(commentLikeObj);
        }else{
            commentObj.setCommentLikeCount(commentLikeCount - 1); // 좋아요 수 1 차감
            commentLikeRepository.deleteById(commentLike.get().getCommentLikeId()); // db에서 삭제
        }
    }
}