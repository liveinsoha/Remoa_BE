package Remoa.BE.Comment.Repository;

import Remoa.BE.Comment.Domain.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {
}
