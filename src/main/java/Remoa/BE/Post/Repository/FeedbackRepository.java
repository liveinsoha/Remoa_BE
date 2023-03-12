package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.Feedback;
import Remoa.BE.Member.Domain.FeedbackBookmark;
import Remoa.BE.Member.Domain.FeedbackLike;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedbackRepository {

    private final EntityManager em;

    public void saveFeedback(Feedback feedback) {
        em.persist(feedback);
    }

    public Optional<Feedback> findByFeedbackId(Long feedbackId) {
        return Optional.ofNullable(em.find(Feedback.class, feedbackId));
    }

    /**
     * 포스트 별 댓글을 찾아오기 위한 메서드
     * @param post
     * @return List<Feedback>
     */
    public List<Feedback> findByPost(Post post) {
        return em.createQuery("select c from Feedback c where c.post = :post", Feedback.class)
                .setParameter("post", post)
                .getResultList();
    }

    public void saveFeedbackLike(FeedbackLike feedbackLike) {
        em.persist(feedbackLike);
    }

    /**
     * feedbackLikeAction 메서드를 실행하기 전 이미 해당 댓글에 대한 좋아요를 했는지 검증->service 단에서 return 값의 null이면 좋아요 가능.
     * 혹은 좋아요 취소를 위해 사용할 수도 있다.
     */
    public Optional<FeedbackLike> findMemberCommendLike(Member member, Feedback feedback) {
        return em.createQuery("select cl from FeedbackLike cl " +
                        "where cl.feedback = :feedback and cl.member = :member", FeedbackLike.class)
                .setParameter("feedback", feedback)
                .setParameter("member", member)
                .getResultStream()
                .findAny();
    }

    public Integer findFeedbackLike(Feedback feedback) {
        return em.createQuery("select cl from FeedbackLike cl where cl.feedback = :feedback", FeedbackLike.class)
                .setParameter("feedback", feedback)
                .getResultList()
                .size();
    }

    public void saveFeedbackBookmark(FeedbackBookmark feedbackBookmark) {
        em.persist(feedbackBookmark);
    }

    /**
     * feedbackBookmarkAction 메서드를 실행하기 전 이미 해당 댓글에 대한 북마크를 했는지 검증->service 단에서 return 값의 null이면 북마크 가능.
     * 혹은 북마크 해제를 위해 사용할 수도 있다.
     */
    public Optional<FeedbackBookmark> findMemberCommendBookmark(Member member, Feedback feedback) {
        return em.createQuery("select cb from FeedbackBookmark cb " +
                        "where cb.feedback = :feedback and cb.member = :member", FeedbackBookmark.class)
                .setParameter("feedback", feedback)
                .setParameter("member", member)
                .getResultStream()
                .findAny();
    }

    /* //필요없을 거 같아서 주석처리...
    public Integer findFeedbackBookmark(Feedback feedback) {
        return em.createQuery("select cb from FeedbackBookmark cb where cb.feedback = :feedback", FeedbackBookmark.class)
                .setParameter("feedback", feedback)
                .getResultList()
                .size();
    }*/
}
