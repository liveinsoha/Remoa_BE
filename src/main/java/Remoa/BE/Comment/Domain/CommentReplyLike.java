package Remoa.BE.Comment.Domain;

import Remoa.BE.Member.Domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Member가 CommentReply(대댓글)을 Like할 때 사용
 */
@Getter
@Setter
@Entity
public class CommentReplyLike {

    @Id
    @GeneratedValue
    @Column(name = "comment_reply_like_id")
    private Long commentReplyLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_reply_id")
    private CommentReply commentReply;

    public static CommentReplyLike createCommentReplyLike(Member member, CommentReply commentReply) {
        CommentReplyLike commentReplyLike = new CommentReplyLike();
        commentReplyLike.setCommentReply(commentReply);
        commentReplyLike.setMember(member);
        return commentReplyLike;
    }
}
