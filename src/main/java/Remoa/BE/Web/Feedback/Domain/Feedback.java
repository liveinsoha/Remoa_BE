package Remoa.BE.Web.Feedback.Domain;

import Remoa.BE.Web.CommentFeedback.Domain.CommentFeedback;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Post;
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
    private String content;

    /**
     * Feedback이 작성된 시간
     */
    @Column(name = "feedback_time")
    private LocalDateTime feedbackTime;

    /**
     * Feedback의 좋아요 숫자
     */
    @Column(name = "like_count")
    private Integer likeCount = 0;


    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<FeedbackLike> feedbackLikes;

    private Boolean deleted = Boolean.FALSE;


    public static Feedback createFeedback(Post post, Member member, Integer pageNumber, String content, LocalDateTime time) {
        Feedback feedbackObj = new Feedback();
        feedbackObj.setPost(post);
        feedbackObj.setMember(member);
        feedbackObj.setPageNumber(pageNumber);
        feedbackObj.setContent(content);
        feedbackObj.setLikeCount(0);
        feedbackObj.setFeedbackTime(time);
        return feedbackObj;
    }
}