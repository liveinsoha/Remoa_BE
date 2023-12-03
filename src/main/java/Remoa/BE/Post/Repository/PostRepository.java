package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.PostLike;
import Remoa.BE.Post.Domain.PostScarp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

    public void savePost(Post post) {
        em.persist(post);
    }

    public void modifyPost(Post post) {
        em.merge(post);
    }

    public List<Post> findAll(){
        return em.createQuery("SELECT p FROM Post p", Post.class).getResultList();
    }


    public Optional<Post> findOne(Long postId) { // 위 findByPostId와 거의 같음 반환형을 Optional<Post>로 하기 위함
        return Optional.ofNullable(em.find(Post.class, postId));
    }

    public List<Post> findByMember(Member member) {
        return em.createQuery("select p from Post p where p.member = :member", Post.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<Post> findByTitleContaining(String name){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);
        query.select(root).where(cb.like(root.get("title"), "%" + name + "%"));
        return em.createQuery(query).getResultList();
    }

    public void savePostScrap(PostScarp postScarp) {
        em.persist(postScarp);
    }

    public Optional<PostScarp> findScrapedPost(Member member) {
        return em.createQuery("select ps from PostScarp ps where ps.member = :member", PostScarp.class)
                .setParameter("member", member)
                .getResultStream()
                .findAny();
    }

    public Optional<PostLike> findLikedPost(Member member) {
        return em.createQuery("select pl from PostLike pl where pl.member = :member", PostLike.class)
                .setParameter("member", member)
                .getResultStream()
                .findAny();
    }

    public List<Post> findPostsByCategory(Category category) {
        return em.createQuery("select p from Post p where p.category = :category", Post.class)
                .setParameter("category", category)
                .getResultList();
    }

    public void saveComment(Comment comment) {
        em.persist(comment);
    }

    public void deletePost(Long postId) {
        Post post = em.find(Post.class, postId);
        em.remove(post);
    }

    public void deletePostByMember(Member member) {
        em.createQuery("delete from Post p where p.member = :member")
                .setParameter("member", member)
                .executeUpdate();
    }
}
