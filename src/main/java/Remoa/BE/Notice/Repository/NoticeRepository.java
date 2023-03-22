package Remoa.BE.Notice.Repository;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.Post.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice,Long> {

}
