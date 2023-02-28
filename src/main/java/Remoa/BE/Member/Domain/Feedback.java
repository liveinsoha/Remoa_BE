package Remoa.BE.Member.Domain;

import Remoa.BE.Post.Domain.Post;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Where(clause = "deleted = false")
public class Feedback {

    @Id
    @GeneratedValue
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
     * Feedback이 작성된 시간
     */
    @Column(name = "feedback_time")
    private String feedbackTime;

    /**
     * Feedback의 좋아요 숫자
     */
    @Column(name = "feedback_like_count")
    private Integer feedbackLikeCount;

    private Boolean deleted = Boolean.FALSE;
}