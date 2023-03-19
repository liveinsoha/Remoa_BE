package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.*;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.CommentPagingRepository;
import Remoa.BE.Post.Repository.CommentLikeRepository;
import Remoa.BE.Post.Repository.CommentRepository;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentLikeRepository commentLikeRepository;
    private final PostService postService;
    private final CommentPagingRepository commentPagingRepository;

    @Transactional
    public Long writeComment(Comment comment) {
        commentRepository.saveComment(comment);
        return comment.getCommentId();
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

    @Transactional
    public Long commentBookmarkAction(Comment comment, Member member) {
        CommentBookmark commentBookmark = CommentBookmark.createCommentBookmark(member, comment);
        commentRepository.saveCommentBookmark(commentBookmark);
        return commentBookmark.getCommentBookmarkId();
    }

    @Transactional
    public void registerComment(Member member, String comment, Long postId, Long commentId){

        Comment parentComment = null;

        if (commentId != null) {
            parentComment = commentRepository.findByCommentId(commentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));
        }

        Comment commentObj = new Comment();
//        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Post post = postService.findOne(postId);

        commentObj.setPost(post);
        commentObj.setMember(member);
        commentObj.setParentComment(parentComment); //대댓글인 경우 원 댓글의 Feedback, 댓글인 경우 null
        commentObj.setComment(comment);
        commentObj.setCommentLikeCount(0);
        commentObj.setCommentedTime(LocalDateTime.now());
        commentRepository.saveComment(commentObj);

    }

    @Transactional
    public void modifyComment(String comment, Long commentId){
        Comment commentObj = findOne(commentId);
        commentObj.setComment(comment);
        commentRepository.updateComment(commentObj);
    }


    @Transactional
    public void deleteComment(Long commentId){
        Comment commentObj = findOne(commentId);
        commentRepository.deleteComment(commentObj);
    }

    public List<Comment> getRecentThreeCommentsExceptReply(Post post) {
        PageRequest pageable = PageRequest.of(0, 3, Sort.by("commentedTime").descending());
        return commentPagingRepository.findByParentCommentIsNullAndPost(pageable, post).getContent();
    }

    public List<Comment> getParentCommentsReply(Comment parentComment) {
        return commentRepository.findRepliesOfParentComment(parentComment);
    }
    @Transactional
    public CommentLike getCommentLikeByMemberIdAndCommentId(Long memberId, Long commentId) {
        return commentLikeRepository.findByMemberMemberIdAndCommentCommentId(memberId, commentId);
    }

    @Transactional
    public void likeComment(Long memberId, Member myMember, Long commentId){
        Comment commentObj = findOne(commentId);
        Integer commentLikeCount = commentObj.getCommentLikeCount();

        // CommentLike를 db에서 조회해보고 조회 결과가 null이면 like+=1, CommentLike 엔티티 생성
        // null이 아니면 like -= 1, 조회결과인 해당 CommentLike 엔티티 삭제
        CommentLike commentLike = getCommentLikeByMemberIdAndCommentId(memberId, commentId);
        if(commentLike == null){
            commentObj.setCommentLikeCount(commentLikeCount + 1); // 좋아요 수 1 증가
            CommentLike commentLikeObj = CommentLike.createCommentLike(myMember, commentObj);
            commentLikeRepository.save(commentLikeObj);
        }else{
            commentObj.setCommentLikeCount(commentLikeCount - 1); // 좋아요 수 1 차감
            commentLikeRepository.deleteById(commentLike.getCommentLikeId()); // db에서 삭제
        }
    }
}