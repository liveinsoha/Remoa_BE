package Remoa.BE.Web.Inquiry.Repository;

import Remoa.BE.Web.Inquiry.Domain.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {

    @Modifying
    @Query("UPDATE InquiryReply ir SET ir.author = :newNick WHERE ir.author = :oldNick")
    void modifyingInquiryReplyAuthor(@Param("oldNick") String oldNick, @Param("newNick") String newNick);

}
