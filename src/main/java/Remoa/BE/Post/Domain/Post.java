package Remoa.BE.Post.Domain;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.Member;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "deleted = false")
public class Post {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long postId;

    /**
     * 해당 Post를 쓴 작성자(Member)
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * Post 제목
     */
    private String title;

    /**
     * 참여 공모전의 이름
     */
    @Column(name = "contest_name")
    private String contestName;

    /**
     * 참여한 공모전의 마감 기한
     */
    private String deadline;

    /**
     * 참여한 공모전의 수상 내역
     */
    @Column(name = "contest_award")
    private Boolean ContestAward;

    /**
     * pm쪽에 문의해야할듯.
     */
    @Column(name = "contest_aware_type")
    private String contestAwareType;

    /**
     * Post에 대한 좋아요 수
     */
    @Column(name = "like_count")
    private Integer likeCount;

    /**
     * Post가 작성된 시간
     */
    @Column(name = "posting_time")
    private String postingTime;

    /**
     * Post의 조회수
     */
    private Integer views;

    /**
     * Post에 작성되어진 Comment
     */
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    /**
     * Post에서 쓰인 files
     */
    @OneToMany(mappedBy = "post")
    private List<UploadFile> uploadFiles = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostScarp> postScarps = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikes = new ArrayList<>();

    /**
     * 작성한 Post의 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Boolean deleted = Boolean.FALSE;
}
