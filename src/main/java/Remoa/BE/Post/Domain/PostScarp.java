package Remoa.BE.Post.Domain;

import Remoa.BE.Member.Domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted = false")
public class PostScarp {

    @Id
    @GeneratedValue
    @Column(name = "post_scrap_id")
    private Long postScrapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "scrap_time")
    private LocalDateTime scrapTime;

    private Boolean deleted = Boolean.FALSE;

    public static PostScarp createPostScrap(Member member, Post post) {
        PostScarp postScrap = new PostScarp();
        postScrap.setPost(post);
        postScrap.setScrapTime(LocalDateTime.now());
        postScrap.setMember(member);

        return postScrap;
    }
}
