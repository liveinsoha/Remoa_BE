package Remoa.BE.Web.Post.Repository;

import Remoa.BE.Web.Post.Domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MyReferenceRepository extends JpaRepository<Post,Long> {

    List<Post>  findByMemberMemberIdAndCategoryCategoryId(Long memberId ,Long categoryId);

    @Override
    void deleteAll(Iterable<? extends Post> entities);
}
