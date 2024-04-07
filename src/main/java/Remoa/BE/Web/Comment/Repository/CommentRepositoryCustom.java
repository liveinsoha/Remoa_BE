package Remoa.BE.Web.Comment.Repository;

import Remoa.BE.Web.Comment.Domain.CommentLike;
import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.Member.Domain.CommentBookmark;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Post;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {

    Optional<Comment> findOne(Long id);
    void saveComment(Comment comment);
    Optional<Comment> findByCommentId(Long commentId);
    List<Comment> findByPost(Post post);
    void saveCommentLike(CommentLike commentLike);
    Optional<CommentLike> findMemberCommendLike(Member member, Comment comment);
    Integer findCommentLike(Comment comment);
    void saveCommentBookmark(CommentBookmark commentBookmark);
    Optional<CommentBookmark> findMemberCommendBookmark(Member member, Comment comment);
    List<Comment> findRepliesOfParentComment(Comment parentComment);
    void updateComment(Comment newComment);
    void deleteComment(Comment comment);
    List<Comment> findAllByMember(Member member);
    void deleteCommentByMember(Member member);
    void deleteChildCommentByParentFeedback(Comment comment);
}
