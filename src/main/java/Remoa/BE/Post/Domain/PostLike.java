package Remoa.BE.Post.Domain;

import Remoa.BE.Member.Domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted = false")
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
