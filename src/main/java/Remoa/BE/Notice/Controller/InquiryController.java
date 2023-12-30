package Remoa.BE.Notice.Controller;

import Remoa.BE.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Notice.Dto.Res.ResNoticeDto;
import Remoa.BE.Notice.Service.InquiryService;
import Remoa.BE.Notice.domain.Inquiry;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.exception.CustomMessage;
import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

        return successResponse(CustomMessage.OK, inquiryService.getInquiry(pageNumber));

    }
    @GetMapping("/inquiry/view")
    public ResponseEntity<Object> getInquiryDetail(@RequestParam int view,
                                                  HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String sessionKey = "InquiryViewed_" + view;

            if (session.getAttribute(sessionKey) == null) {
                inquiryService.inquiryViewCount(view);
                session.setAttribute(sessionKey, true);
            }

            return successResponse(CustomMessage.OK, inquiryService.getInquiryView(view));
        } catch (Exception e) {
            return errorResponse(CustomMessage.NO_ID);
        }
    }
}