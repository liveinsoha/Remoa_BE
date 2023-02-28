package Remoa.BE.Member.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Member가 Feedback을Bookmark할 때 사용
 */
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackBookmark {

    @Id
    @GeneratedValue
    @Column(name = "feedback_bookmark_id")
    private Long feedbackBookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    public static FeedbackBookmark createFeedbackBookmark(Member member, Feedback feedback) {
        FeedbackBookmark feedbackBookmark = new FeedbackBookmark();
        feedbackBookmark.setFeedback(feedback);
        feedbackBookmark.setMember(member);

        return feedbackBookmark;
    }
}
