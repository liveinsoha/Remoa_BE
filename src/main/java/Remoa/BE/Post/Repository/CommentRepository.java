package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.*;
import Remoa.BE.Post.Domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;
    public Optional<Comment> findOne(Long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }
    public void saveComment(Comment comment) {
        em.persist(comment);
    }

    public Optional<Comment> findByCommentId(Long commentId) {
        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    /**
     * 포스트 별 댓글을 찾아오기 위한 메서드
     * @param post
     * @return List<Comment>
     */
    public List<Comment> findByPost(Post post) {
        return em.createQuery("select c from Comment c where c.post = :post order by c.commentedTime desc",
                        Comment.class)
                .setParameter("post", post)
                .getResultList();
    }

    public void saveCommentLike(CommentLike commentLike) {
        em.persist(commentLike);
    }

    /**
     * commentLikeAction 메서드를 실행하기 전 이미 해당 댓글에 대한 좋아요를 했는지 검증->service 단에서 return 값의 null이면 좋아요 가능.
     * 혹은 좋아요 취소를 위해 사용할 수도 있다.
     */
    public Optional<CommentLike> findMemberCommendLike(Member member, Comment comment) {
        return em.createQuery("select cl from CommentLike cl " +
                        "where cl.comment = :comment and cl.member = :member", CommentLike.class)
                .setParameter("comment", comment)
                .setParameter("member", member)
                .getResultStream()
                .findAny();
    }

    public Integer findCommentLike(Comment comment) {
        return em.createQuery("select cl from CommentLike cl where cl.comment = :comment", CommentLike.class)
                .setParameter("comment", comment)
                .getResultList()
                .size();
    }

    public void saveCommentBookmark(CommentBookmark commentBookmark) {
        em.persist(commentBookmark);
    }

    /**
     * commentBookmarkAction 메서드를 실행하기 전 이미 해당 댓글에 대한 북마크를 했는지 검증->service 단에서 return 값의 null이면 북마크 가능.
     * 혹은 북마크 해제를 위해 사용할 수도 있다.
     */
    public Optional<CommentBookmark> findMemberCommendBookmark(Member member, Comment comment) {
        return em.createQuery("select cb from CommentBookmark cb " +
                        "where cb.comment = :comment and cb.member = :member", CommentBookmark.class)
                .setParameter("comment", comment)
                .setParameter("member", member)
                .getResultStream()
                .findAny();
    }

    /* //필요없을 거 같아서 주석처리...
    public Integer findCommentBookmark(Comment comment) {
        return em.createQuery("select cb from CommentBookmark cb where cb.comment = :comment", CommentBookmark.class)
                .setParameter("comment", comment)
                .getResultList()
                .size();
    }*/

    public List<Comment> findRepliesOfParentComment(Comment parentComment) {
        return em.createQuery("select c from Comment c " +
                        "where c.parentComment = :comment order by c.commentedTime desc", Comment.class)
                .setParameter("comment", parentComment)
                .getResultList();
    }

    public void updateComment(Comment newComment){
        em.merge(newComment);
    }

    public void deleteComment(Comment comment){
        em.remove(comment);
    }

    public List<Comment> findAllByMember(Member member) {
        return em.createQuery("select c from Comment c " +
                "where c.member = :member", Comment.class)
                .setParameter("member", member)
                .getResultList();
    }

    public void deleteCommentByMember(Member member) {
        em.createQuery("delete from Comment c where c.member = :member")
                .setParameter("member", member)
                .executeUpdate();
    }

    public void deleteChildCommentByParentFeedback(Comment comment){
        em.createQuery("delete from Comment c where c.parentComment = :comment")
                .setParameter("comment", comment)
                .executeUpdate();
    }
}
