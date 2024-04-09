package Remoa.BE.Web.Notice.Service;

import Remoa.BE.Web.Notice.Dto.Req.ReqInquiryDto;
import Remoa.BE.Web.Notice.Dto.Res.ResAllInquiryDto;
import Remoa.BE.Web.Notice.Dto.Res.ResInquiryDto;
import Remoa.BE.Web.Notice.Repository.InquiryRepository;
import Remoa.BE.Web.Notice.domain.Inquiry;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public void registerInquiry(ReqInquiryDto reqInquiryDto, String enrollNickname) {

        inquiryRepository.save(reqInquiryDto.toEntityInquiry(enrollNickname));
    }

    public HashMap<String, Object> getInquiry(int pageNumber) {

        HashMap<String, Object> resultMap = new HashMap<>();

        int INQUIRY_NUMBER = 5;

        Page<Inquiry> notices = inquiryRepository.findAll(PageRequest.of(pageNumber, INQUIRY_NUMBER, Sort.by("postingTime").descending()));

        resultMap.put("inquiries", notices.stream().map(ResInquiryDto::new).collect(Collectors.toList())); //조회한 레퍼런스들
        resultMap.put("totalPages", notices.getTotalPages()); //전체 페이지의 수
        resultMap.put("totalOfAllNotices", notices.getTotalElements()); //모든 레퍼런스의 수
        resultMap.put("totalOfPageElements", notices.getNumberOfElements()); //현 페이지의 레퍼런스 수

        return resultMap;
    }

    public ResAllInquiryDto getInquiryView(int view) {
        return inquiryRepository.findById((long) view).map(ResAllInquiryDto::new).orElseThrow(() ->
                new BaseException(CustomMessage.NO_ID));
    }

    @Transactional
    public void inquiryViewCount(int view) {
        Inquiry inquiry = inquiryRepository.findById((long) view).orElseThrow(() ->
                new BaseException(CustomMessage.NO_ID));
        inquiry.addInquiryViewCount(inquiry.getView());
        inquiryRepository.save(inquiry);

    }

    @Transactional
    public void modifying_Inquiry_NickName(String newNick, String oldNick) {
        inquiryRepository.modifyingInquiryAuthor(newNick, oldNick);
    }
}
