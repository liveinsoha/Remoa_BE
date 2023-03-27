package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.FeedbackLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackLikeRepository extends JpaRepository<FeedbackLike, Long> {
    Optional<FeedbackLike> findByMemberMemberIdAndFeedbackFeedbackId(Long memberId, Long feedbackId);
}