package Remoa.BE.Web.Inquiry.Service;


import Remoa.BE.Web.Inquiry.Domain.Inquiry;
import Remoa.BE.Web.Inquiry.Domain.InquiryReply;
import Remoa.BE.Web.Inquiry.Dto.Req.ReqInquiryReplyDto;
import Remoa.BE.Web.Inquiry.Repository.InquiryReplyRepository;
import Remoa.BE.Web.Inquiry.Repository.InquiryRepository;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryReplyService {

    private final InquiryReplyRepository inquiryReplyRepository;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public void registerInquiryReply(ReqInquiryReplyDto req, Long inquiryId, String enrollNickname) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BaseException(CustomMessage.NO_ID));

        InquiryReply inquiryReply = req.toEntityInquiryReply(enrollNickname, inquiry);
        inquiryReplyRepository.save(inquiryReply);

        inquiry.setReplied(true);
    }

}
