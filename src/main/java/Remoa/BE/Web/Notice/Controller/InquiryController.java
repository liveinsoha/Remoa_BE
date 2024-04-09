package Remoa.BE.Web.Notice.Controller;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.Web.Notice.Dto.Req.ReqInquiryDto;
import Remoa.BE.Web.Notice.Dto.Res.ResAllInquiryDto;
import Remoa.BE.Web.Notice.Service.InquiryService;
import Remoa.BE.config.auth.MemberDetails;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import Remoa.BE.exception.response.BaseResponse;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Tag(name = "문의 기능 Test Completed", description = "문의 기능 API")
@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final MemberService memberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의가 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/inquiry")
    @Operation(summary = "문의 등록 Test Completed", description = "문의를 등록합니다.")
    public ResponseEntity<Object> postInquiry(@Validated @RequestBody ReqInquiryDto inquiryDto,
                                              @AuthenticationPrincipal MemberDetails memberDetails) {
        Member myMember = memberService.findOne(memberDetails.getMemberId());
        inquiryService.registerInquiry(inquiryDto, myMember.getNickname());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 목록을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/inquiry")
    @Operation(summary = "문의 목록 조회 Test Completed", description = "페이지별 문의 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<HashMap<String, Object>>> getInquiry(@RequestParam(required = false, defaultValue = "1", name = "page") int pageNumber) {
        pageNumber -= 1;
        if (pageNumber < 0) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //    return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        BaseResponse<HashMap<String, Object>> response = new BaseResponse<>(CustomMessage.OK, inquiryService.getInquiry(pageNumber));
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, inquiryService.getInquiry(pageNumber));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 상세 정보를 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "해당 문의가 존재하지 않습니다.")
    })
    @GetMapping("/inquiry/view")
    @Operation(summary = "문의 상세 조회 Test Completed", description = "특정 문의의 상세 정보를 조회합니다.")
    public ResponseEntity<BaseResponse<ResAllInquiryDto>> getInquiryDetail(@RequestParam int view,
                                                                           HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionKey = "InquiryViewed_" + view;

        if (session.getAttribute(sessionKey) == null) {
            inquiryService.inquiryViewCount(view);
            session.setAttribute(sessionKey, true);
        }

        BaseResponse<ResAllInquiryDto> response = new BaseResponse<>(CustomMessage.OK, inquiryService.getInquiryView(view));
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, inquiryService.getInquiryView(view));

    }
}