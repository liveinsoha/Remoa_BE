package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.CommentBookmark;
import Remoa.BE.Member.Domain.CommentLike;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.CommentRepository;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

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

    @Transactional
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
    public Post registerComment(Member member, String comment, Long postId, Long commentId){

        Comment parentComment = null;

        if (commentId != null) {
            parentComment = commentRepository.findByCommentId(commentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));
        }

        Comment commentObj = new Comment();
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Post post = postService.findOne(postId);

        commentObj.setPost(post);
        commentObj.setMember(member);
        commentObj.setParentComment(parentComment); //대댓글인 경우 원 댓글의 Feedback, 댓글인 경우 null
        commentObj.setComment(comment);
        commentObj.setCommentLikeCount(0);
        commentObj.setCommentedTime(formatDate);
        commentRepository.saveComment(commentObj);

        return post;
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
}