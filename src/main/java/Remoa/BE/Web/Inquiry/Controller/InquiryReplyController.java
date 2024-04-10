package Remoa.BE.Web.Inquiry.Controller;

import Remoa.BE.Web.Inquiry.Dto.Req.ReqInquiryDto;
import Remoa.BE.Web.Inquiry.Dto.Req.ReqInquiryReplyDto;
import Remoa.BE.Web.Inquiry.Service.InquiryReplyService;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.config.auth.MemberDetails;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "문의 답변 기능", description = "문의 답변 기능 API")
@RestController
@RequiredArgsConstructor
public class InquiryReplyController {

    private final MemberService memberService;
    private final InquiryReplyService inquiryReplyService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 답글이 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = MessageUtils.FORBIDDEN,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/inquiry/{inquiry_id}/reply")
    @Operation(summary = "문의 답글 등록 ", description = "문의 답글을 등록합니다.")
    public ResponseEntity<Object> postInquiry(@Validated @RequestBody ReqInquiryReplyDto inquiryReplyDto,
                                              @PathVariable("inquiry_id") Long inquiryId,
                                              @AuthenticationPrincipal MemberDetails memberDetails) {

        Member myMember = memberService.findOne(memberDetails.getMemberId());
        inquiryReplyService.registerInquiryReply(inquiryReplyDto, inquiryId, myMember.getNickname());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
