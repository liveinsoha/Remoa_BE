package Remoa.BE.Member.Domain;

import Remoa.BE.Post.Domain.Post;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Where(clause = "deleted = false")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    /**
     * Feedback이 속해있는 Post
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * Feedback을 작성한 Member
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "page_number")
    private Integer pageNumber;

    /**
     * Feedback의 내용
     */
    private String feedback;

    /**
     * 대댓글 기능을 위해 부모 댓글과의 연관관계 세팅. 부모댓글인 경우 null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_feedback_id")
    private Feedback parentFeedback = null;


    /**
     * Feedback이 작성된 시간
     */
    @Column(name = "feedback_time")
    private LocalDateTime feedbackTime;

    /**
     * Feedback의 좋아요 숫자
     */
    @Column(name = "feedback_like_count")
    private Integer feedbackLikeCount = 0;

    @OneToOne
    private CommentFeedback commentFeedback;

    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<FeedbackLike> feedbackLikes;

    private Boolean deleted = Boolean.FALSE;
}