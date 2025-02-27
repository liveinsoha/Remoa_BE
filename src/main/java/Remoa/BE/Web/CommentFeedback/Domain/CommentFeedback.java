package Remoa.BE.Web.CommentFeedback.Domain;

import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.Feedback.Domain.Feedback;
import Remoa.BE.Web.Member.Domain.ContentType;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 마이페이지-내 활동 관리에 쓰이는 Comment와 Feedback을 구분 없이 최신순으로 조회하기 위한 entity.
 */
@Builder
@Entity
@Getter
@Setter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE comment_feedback SET deleted = true WHERE comment_feedback_id = ?")
@NoArgsConstructor
@AllArgsConstructor
public class CommentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_feedback_id")
    private Long commentFeedbackId;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne
    private Comment comment;

    @OneToOne
    private Feedback feedback;

    private LocalDateTime time;

    @Builder.Default
    private Boolean deleted = Boolean.FALSE;
}
