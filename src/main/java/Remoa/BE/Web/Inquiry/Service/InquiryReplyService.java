package Remoa.BE.Web.Inquiry.Service;


import Remoa.BE.Web.Inquiry.Domain.Inquiry;
import Remoa.BE.Web.Inquiry.Domain.InquiryReply;
import Remoa.BE.Web.Inquiry.Dto.Req.ReqInquiryDto;
import Remoa.BE.Web.Inquiry.Dto.Req.ReqInquiryReplyDto;
import Remoa.BE.Web.Inquiry.Repository.InquiryReplyRepository;
import Remoa.BE.Web.Inquiry.Repository.InquiryRepository;
import Remoa.BE.Web.Member.Domain.Member;
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


    @Transactional
    public void updateInquiryReply(Long replyId, ReqInquiryReplyDto updateReplyDto, Member member) {
        InquiryReply inquiryReply = inquiryReplyRepository.findById(replyId)
                .orElseThrow(() -> new BaseException(CustomMessage.NO_ID));


        if (!inquiryReply.getAuthor().equals(member.getNickname())) {
            throw new BaseException(CustomMessage.CAN_NOT_ACCESS);
        }

        inquiryReply.updateInquiry(updateReplyDto);
    }

    @Transactional
    public void modifying_Inquiry_Reply_NickName(String newNick, String oldNick) {
        inquiryReplyRepository.modifyingInquiryReplyAuthor(newNick, oldNick);
    }

}
