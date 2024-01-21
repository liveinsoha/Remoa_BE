package Remoa.BE.Notice.Repository;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.Post.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice,Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Notice n SET n.author = :newNick WHERE n.author = :oldNick")
    void modifyingNoticeAuthor(@Param("oldNick") String oldNick, @Param("newNick")String newNick);

}
