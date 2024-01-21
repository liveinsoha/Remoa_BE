package Remoa.BE.Notice.Repository;

import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry,Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Inquiry i SET i.author = :newNick WHERE i.author = :oldNick")
    void modifyingInquiryAuthor(@Param("oldNick") String oldNick, @Param("newNick")String newNick);
}
