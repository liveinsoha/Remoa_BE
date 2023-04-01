package Remoa.BE.Member.Domain;

import Remoa.BE.Post.Domain.Post;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Where(clause = "deleted = false")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    /**
     * Comment가 속해있는 Post
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * Comment를 작성한 Member
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * Comment의 내용
     */
    private String comment;

    /**
     * 대댓글 기능을 위해 부모 댓글과의 연관관계 세팅. 부모댓글인 경우 null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment = null;

    /**
     * Comment가 작성된 시간
     */
    @Column(name = "commented_time")
    private LocalDateTime commentedTime;

    /**
     * Comment의 좋아요 숫자
     */
    @Column(name = "comment_like_count")
    private Integer commentLikeCount = 0;

    @OneToOne
    private CommentFeedback commentFeedback;

    @OneToMany(mappedBy = "comment", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CommentLike> commentLikes;

    private Boolean deleted = Boolean.FALSE;
}