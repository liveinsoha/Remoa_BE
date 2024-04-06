package Remoa.BE.Comment.Repository;

import Remoa.BE.Comment.Domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByMemberMemberIdAndCommentCommentId(Long memberId, Long commentId);
}