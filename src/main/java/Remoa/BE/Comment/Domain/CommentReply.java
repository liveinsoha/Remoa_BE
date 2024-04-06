package Remoa.BE.Comment.Domain;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.CommentFeedback;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class CommentReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_reply_id")
    private Long commentReplyId;

    /**
     * CommentReply 속해있는 Post
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * CommentReply 작성한 Member
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * CommentReply 내용
     */
    private String content;

    /**
     * 대댓글 기능을 위해 부모 댓글과의 연관관계 세팅. 부모댓글인 경우 null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * Comment가 작성된 시간
     */
    @Column(name = "commented_time")
    private LocalDateTime commentRepliedTime;

    /**
     * Comment의 좋아요 숫자
     */
    @Column(name = "comment_like_count")
    private Integer commentReplyLikeCount = 0;

    @OneToOne
    private CommentFeedback commentFeedback;

    @OneToMany(mappedBy = "comment", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CommentReplyLike> commentReplyLikes;

    private Boolean deleted = Boolean.FALSE;
}
