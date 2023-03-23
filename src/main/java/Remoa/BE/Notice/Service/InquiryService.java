package Remoa.BE.Notice.Service;

import Remoa.BE.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Notice.Repository.InquiryRepository;
import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public void registerInquiry(ReqNoticeDto reqNoticeDto){
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle(reqNoticeDto.getTitle());
        inquiry.setContent(reqNoticeDto.getContent());
        inquiry.setPostingTime(LocalDateTime.now());

        inquiryRepository.save(inquiry);
    }

    public Page<Inquiry> getInquiry(int pageNumber){
        int NOTICE_NUMBER = 5;
        Pageable pageable = PageRequest.of(pageNumber, NOTICE_NUMBER);
        return inquiryRepository.findAll(pageable);
    }
}
