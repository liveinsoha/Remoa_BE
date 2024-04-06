package Remoa.BE.Comment.Repository;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Post.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentPagingRepository extends PagingAndSortingRepository<Comment, Long> {

    Page<Comment> findByParentCommentIsNullAndPost(Pageable pageable, Post post);

}
