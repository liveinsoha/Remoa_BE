package Remoa.BE.Web.Post.Repository;

import Remoa.BE.Web.Post.Domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    PostLike findByMemberMemberIdAndPostPostId(Long memberId, Long postId);
}
