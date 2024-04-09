package Remoa.BE.Web.CommentFeedback.Repository;

import Remoa.BE.Web.CommentFeedback.Domain.CommentFeedback;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Category;
import Remoa.BE.Web.Post.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentFeedbackRepository extends JpaRepository<CommentFeedback, Long>, CommentFeedbackCustomRepository {

    Page<CommentFeedback> findByMemberOrderByTimeDesc(Pageable pageable, Member member);


}
