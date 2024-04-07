package Remoa.BE.Web.Notice.Repository;

import Remoa.BE.Web.Notice.domain.Notice;
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
