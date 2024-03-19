package Remoa.BE.Notice.Controller;

import Remoa.BE.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Notice.Dto.Res.ResNoticeDto;
import Remoa.BE.Notice.Service.NoticeService;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.exception.CustomMessage;
import com.amazonaws.services.kms.model.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpRequest;
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

import static Remoa.BE.exception.CustomBody.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/notice")
    @Operation(summary = "공지 등록", description = "공지를 등록합니다.")
    public ResponseEntity<Object> postNotice(@Validated @RequestBody ReqNoticeDto reqNoticeDto) {
        noticeService.registerNotice(reqNoticeDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/notice")
    @Operation(summary = "공지 목록 조회", description = "페이지별 공지 목록을 조회합니다.")
    public ResponseEntity<Object> getNotice(@RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber) {
        pageNumber -= 1;
        if (pageNumber < 0) {
            return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        return successResponse(CustomMessage.OK, noticeService.getNotice(pageNumber));

    }

    @GetMapping("/notice/view")
    @Operation(summary = "공지 상세 조회", description = "특정 공지의 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getNoticeDetail(@RequestParam int view,
                                                  HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String sessionKey = "NoticeViewed_" + view;

            if (session.getAttribute(sessionKey) == null) {
                noticeService.NoticeViewCount(view);
                session.setAttribute(sessionKey, true);
            }

            return successResponse(CustomMessage.OK, noticeService.getNoticeView(view));
        } catch (Exception e) {
            return errorResponse(CustomMessage.NO_ID);
        }
    }
}
