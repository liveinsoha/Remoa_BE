package Remoa.BE.Web.Comment.Domain;

import Remoa.BE.Web.Member.Domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

/**
 * Member가 Comment를 Like할 때 사용
 */
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {

    @Id
    @GeneratedValue
    @Column(name = "comment_like_id")
    private Long commentLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public static CommentLike createCommentLike(Member member, Comment comment) {
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setMember(member);
        return commentLike;
    }
}