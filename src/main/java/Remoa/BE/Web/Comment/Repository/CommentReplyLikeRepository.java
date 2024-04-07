package Remoa.BE.Web.Comment.Repository;

import Remoa.BE.Web.Comment.Domain.CommentReply;
import Remoa.BE.Web.Comment.Domain.CommentReplyLike;
import Remoa.BE.Web.Member.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CommentReplyLikeRepository extends JpaRepository<CommentReplyLike, Long> {

    Optional<CommentReplyLike> findByMemberAndCommentReply(Member member, CommentReply commentReply);
}
