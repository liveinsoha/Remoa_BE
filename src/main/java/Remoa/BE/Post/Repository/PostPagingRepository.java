package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostPagingRepository extends PagingAndSortingRepository<Post, Long> {
    /**
     * 파라메터로 넘어온 member의 pageable에 명시된 옵션에 따라서
     *
     * @param pageable
     * @param member
     * @return
     */
    Page<Post> findAllByMemberOrderByPostingTimeDesc(Pageable pageable, Member member);

    Page<Post> findAllByMemberOrderByPostingTimeAsc(Pageable pageable, Member member);

    Page<Post> findAllByMemberOrderByLikeCountDesc(Pageable pageable, Member member);

    Page<Post> findAllByMemberOrderByScrapCountDesc(Pageable pageable, Member member);

    Page<Post> findAllByMember(Pageable pageable, Member member);

}
