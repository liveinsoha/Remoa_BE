package Remoa.BE.Post.Repository;

import Remoa.BE.Post.Domain.PostScarp;
import Remoa.BE.Post.Service.PostService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScrapRepository extends JpaRepository<PostScarp, Long> {
    PostScarp findByMemberMemberIdAndPostPostId(Long memberId, Long postId);
}
