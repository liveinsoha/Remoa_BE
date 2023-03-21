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

    Page<Post> findAllByMemberOrderByPostingTimeDesc(Pageable pageable, Member member);

    Page<Post> findAllByMemberOrderByViewsDesc(Pageable pageable, Member member);

    Page<Post> findAllByMemberOrderByLikeCountDesc(Pageable pageable, Member member);

    Page<Post> findAllByMemberOrderByScrapCountDesc(Pageable pageable, Member member);

    Page<Post> findAllByMemberAndCategory(Pageable pageable, Member member, Category category);

    Page<Post> findAllByMember(Pageable pageable, Member member);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByCategory(Pageable pageable, Category category);

}
