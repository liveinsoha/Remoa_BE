package Remoa.BE.Web.Feedback.Domain;

import Remoa.BE.Web.Member.Domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FeedbackReplyLike {

    @Id
    @GeneratedValue
    @Column(name = "feedback_reply_like_id")
    private Long feedbackReplyLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_reply_id")
    private FeedbackReply feedbackReply;

    public static FeedbackReplyLike createFeedbackReplyLike(Member member, FeedbackReply feedbackReply) {
        FeedbackReplyLike feedbackReplyLike = new FeedbackReplyLike();
        feedbackReplyLike.setFeedbackReply(feedbackReply);
        feedbackReplyLike.setMember(member);
        return feedbackReplyLike;
    }
}