package Remoa.BE.Web.Post.Repository;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.PostScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    PostScrap findByMemberMemberIdAndPostPostId(Long memberId, Long postId);

    Page<PostScrap> findByMemberOrderByScrapTimeDesc(Pageable pageable, Member member);
}
