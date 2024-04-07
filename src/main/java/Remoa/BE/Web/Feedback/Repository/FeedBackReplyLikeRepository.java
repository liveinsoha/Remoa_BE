package Remoa.BE.Web.Feedback.Repository;

import Remoa.BE.Web.Feedback.Domain.FeedbackReply;
import Remoa.BE.Web.Feedback.Domain.FeedbackReplyLike;
import Remoa.BE.Web.Member.Domain.Member;
import org.hibernate.boot.archive.internal.JarProtocolArchiveDescriptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedBackReplyLikeRepository extends JpaRepository<FeedbackReplyLike, Long> {

    Optional<FeedbackReplyLike >findByMemberAndFeedbackReply(Member member, FeedbackReply feedbackReply);
}
