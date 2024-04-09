package Remoa.BE.Web.CommentFeedback.Repository;

import Remoa.BE.Web.CommentFeedback.Domain.CommentFeedback;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Category;
import Remoa.BE.Web.Post.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentFeedbackRepository extends JpaRepository<CommentFeedback, Long>, CommentFeedbackCustomRepository {

    Page<CommentFeedback> findByMemberOrderByTimeDesc(Pageable pageable, Member member);

    @Query("SELECT cf FROM CommentFeedback cf " +
            "INNER JOIN FETCH cf.post p " +
            "WHERE cf.member = :member " +
            "AND cf.time = (SELECT MAX(cf2.time) FROM CommentFeedback cf2 WHERE cf2.post.postId = cf.post.postId) " +
            "ORDER BY cf.time DESC")
    Page<CommentFeedback> findNewestCommentFeedback(Member member, Pageable pageable);



}
