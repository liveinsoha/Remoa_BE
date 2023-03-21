package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * JPA Query Creation을 사용해서 post data sorting & slicing
 */
@Repository
public interface PostPagingRepository extends PagingAndSortingRepository<Post, Long> {

    Page<Post> findByMemberOrderByPostingTimeDesc(Pageable pageable, Member member);

    Page<Post> findByMemberOrderByViewsDesc(Pageable pageable, Member member);

    Page<Post> findByMemberOrderByLikeCountDesc(Pageable pageable, Member member);

    Page<Post> findByMemberOrderByScrapCountDesc(Pageable pageable, Member member);

    Page<Post> findByMemberAndCategoryAndTitleContaining(Pageable pageable, Member member, Category category,String title);

    //"IsNotEmpty" 부분에 "Cannot resolve property 'isNotEmpty'"경고가 나오는 건 JPA의 isNotEmpty 예약어를 intellij가 인식하지 못하고 자바의 프로퍼티로 인식하기 때문. 즉, 무시해도 됨.
    Page<Post> findByMemberAndCategoryAndCommentsIsNotEmpty(Pageable pageable, Member member, Category category);

    //"IsNotEmpty" 부분에 "Cannot resolve property 'isNotEmpty'"경고가 나오는 건 JPA의 isNotEmpty 예약어를 intellij가 인식하지 못하고 자바의 프로퍼티로 인식하기 때문. 즉, 무시해도 됨.
    Page<Post> findByMemberAndCommentsIsNotEmpty(Pageable pageable, Member member);

    Page<Post> findByTitleContaining(Pageable pageable,String title);

    Page<Post> findByCategoryAndTitleContaining(Pageable pageable, Category category,String title);

}
