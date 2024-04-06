package Remoa.BE.Notice.Controller;

import Remoa.BE.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Notice.Dto.Res.NoticeResponseDto;
import Remoa.BE.Notice.Dto.Res.ResAllNoticeDto;
import Remoa.BE.Notice.Dto.Res.ResNoticeDto;
import Remoa.BE.Notice.Service.NoticeService;
import Remoa.BE.Notice.domain.Notice;
import Remoa.BE.config.auth.MemberDetails;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import Remoa.BE.exception.response.BaseResponse;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MessageUtils;
import com.amazonaws.services.kms.model.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Remoa.BE.exception.CustomBody.*;

@Tag(name = "공지 기능", description = "공지 기능 API")
@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지가 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "400", description = MessageUtils.BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = MessageUtils.FORBIDDEN,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/notice")
    @Operation(summary = "공지 등록", description = "공지를 등록합니다.")
    public ResponseEntity<Object> postNotice(@Validated @RequestBody ReqNoticeDto reqNoticeDto,
                                             @AuthenticationPrincipal MemberDetails memberDetails) {

        noticeService.registerNotice(reqNoticeDto, memberDetails.getNickname());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/notice")
    @Operation(summary = "공지 목록 조회", description = "페이지별 공지 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<NoticeResponseDto>> getNotice(@RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber) {
        pageNumber -= 1;
        if (pageNumber < 0) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //  return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }
        BaseResponse<NoticeResponseDto> response = new BaseResponse<>(CustomMessage.OK, noticeService.getNotice(pageNumber));
        return ResponseEntity.ok(response);
        //return successResponse(CustomMessage.OK, noticeService.getNotice(pageNumber));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지 목록을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/notice/view")
    @Operation(summary = "공지 상세 조회", description = "특정 공지의 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getNoticeDetail(@RequestParam int view,
                                                  HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionKey = "NoticeViewed_" + view;

        if (session.getAttribute(sessionKey) == null) { // 조회하지 않은 경우
            noticeService.NoticeViewCount(view); // 조회수 증가
            session.setAttribute(sessionKey, true);
        }

        BaseResponse<ResAllNoticeDto> response = new BaseResponse<>(CustomMessage.OK, noticeService.getNoticeView(view));
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, noticeService.getNoticeView(view));
    }
}
