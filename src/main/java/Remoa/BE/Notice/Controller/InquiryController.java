package Remoa.BE.Notice.Controller;

import Remoa.BE.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Notice.Dto.Res.ResNoticeDto;
import Remoa.BE.Notice.Service.InquiryService;
import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.errorResponse;
import static Remoa.BE.exception.CustomBody.successResponse;

@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/inquiry")
    public ResponseEntity<Object> postInquiry(@Validated @RequestBody ReqNoticeDto reqNoticeDto){
        inquiryService.registerInquiry(reqNoticeDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/inquiry")
    public ResponseEntity<Object> getInquiry(@RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber){
        pageNumber -= 1;
        if (pageNumber < 0) {
            return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }
        Page<Inquiry> inquiries =inquiryService.getInquiry(pageNumber);

        if ((inquiries.getContent().isEmpty()) && (inquiries.getTotalElements() > 0)) {
            return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        Map<String, Object> responseData = new HashMap<>();

        List<Object> result = new ArrayList<>();

        for(Inquiry inquiry:inquiries) {
            ResNoticeDto resNoticeDto = ResNoticeDto.builder()
                    .noticeId(inquiry.getInquiryId())
                    .title(inquiry.getTitle())
                    .postingTime(inquiry.getPostingTime().toLocalDate())
                    .view(10)
                    .build();
            result.add(resNoticeDto);
        }

        responseData.put("inquiries", result); //조회한 레퍼런스들
        responseData.put("totalPages", inquiries.getTotalPages()); //전체 페이지의 수
        responseData.put("totalOfAllNotices", inquiries.getTotalElements()); //모든 레퍼런스의 수
        responseData.put("totalOfPageElements", inquiries.getNumberOfElements()); //현 페이지의 레퍼런스 수

        return successResponse(CustomMessage.OK, responseData);
    }
}