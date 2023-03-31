package Remoa.BE.Post.Domain;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.CommentFeedback;
import Remoa.BE.Member.Domain.Feedback;
import Remoa.BE.Member.Domain.Member;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne
    private UploadFile thumbnail;

    /**
     * 참여 공모전의 이름
     */
    @Column(name = "contest_name")
    private String contestName;

    /**
     * 유튜브 링크
     */
    private String youtubeLink="";

    /**
     * 참여한 공모전의 마감 기한
     */
    private String deadline;


    /**
     * pm쪽에 문의해야할듯.
     */
    @Column(name = "contest_aware_type")
    private String contestAwardType;

    /**
     * Post에 대한 좋아요 수
     */
    @Column(name = "like_count")
    private Integer likeCount = 0;

    /**
     * Post가 작성된 시간
     */
    @Column(name = "posting_time")
    private LocalDateTime postingTime;

    /**
     * Post의 조회수
     */
    private Integer views = 0;

    private Integer scrapCount = 0;

    private Integer pageCount = 1;

    /**
     * Post에 작성되어진 Comment
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<CommentFeedback> commentFeedbacks = new ArrayList<>();

    /**
     * Post에서 쓰인 files
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<UploadFile> uploadFiles;

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
