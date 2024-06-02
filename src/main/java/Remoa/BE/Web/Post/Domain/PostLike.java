package Remoa.BE.Web.Post.Domain;

import Remoa.BE.Web.Member.Domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE post_like SET deleted = true WHERE post_like_id = ?")
@SQLRestriction("deleted = false")
public class PostLike {

    @Id
    @GeneratedValue
    @Column(name = "post_like_id")
    private Long postLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Boolean deleted = Boolean.FALSE;

    public static PostLike createPostLike(Member member, Post post) {
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setMember(member);

        return postLike;
    }
}
