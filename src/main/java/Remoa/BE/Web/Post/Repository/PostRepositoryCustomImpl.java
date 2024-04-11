package Remoa.BE.Web.Post.Repository;

import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Category;
import Remoa.BE.Web.Post.Domain.Post;
import Remoa.BE.Web.Post.Domain.PostLike;
import Remoa.BE.Web.Post.Domain.PostScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom  {

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

    public void savePostScrap(PostScrap postScrap) {
        em.persist(postScrap);
    }

    public Optional<PostScrap> findScrapedPost(Member member, Post post) {
        return em.createQuery("select ps from PostScrap ps where ps.member = :member and ps.post = :post", PostScrap.class)
                .setParameter("member", member)
                .setParameter("post", post)
                .getResultStream()
                .findAny();
    }

    public Optional<PostLike> findLikedPost(Member myMember, Post post) {
        return em.createQuery("select pl from PostLike pl where pl.member = :member and pl.post = :post", PostLike.class)
                .setParameter("member", myMember)
                .setParameter("post", post)
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

    public Optional<Member> findPostedMember(Long postId) {
        return em.createQuery("select p.member from Post p where p.postId = :postId", Member.class)
                .setParameter("postId", postId)
                .getResultStream()
                .findAny();
    }
}
