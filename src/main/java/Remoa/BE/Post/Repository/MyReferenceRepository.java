package Remoa.BE.Post.Repository;

import Remoa.BE.Post.Domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Repository
public interface MyReferenceRepository extends JpaRepository<Post,Long> {

    List<Post>  findByMemberMemberIdAndCategoryCategoryId(Long memberId ,Long categoryId);

    @Override
    void deleteAll(Iterable<? extends Post> entities);
}
