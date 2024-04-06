package Remoa.BE.Comment.Repository;

import Remoa.BE.Comment.Domain.CommentReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReplyLikeRepository extends JpaRepository<CommentReplyLike, Long> {
}
