package Remoa.BE.Member.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Member가 Feedback을 Like할 때 사용
 */
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackLike {

    @Id
    @GeneratedValue
    @Column(name = "feedback_like_id")
    private Long feedbackLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    public static FeedbackLike createFeedbackLike(Member member, Feedback feedback) {
        FeedbackLike feedbackLike = new FeedbackLike();
        feedbackLike.setFeedback(feedback);
        feedbackLike.setMember(member);

        return feedbackLike;
    }
}