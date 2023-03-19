package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.CommentLike;
import Remoa.BE.Member.Domain.FeedbackLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByMemberMemberIdAndCommentCommentId(Long memberId, Long commentId);
}