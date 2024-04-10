package Remoa.BE.Web.Inquiry.Repository;

import Remoa.BE.Web.Inquiry.Domain.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {
}
