package Remoa.BE.Web.Member.Controller;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.Web.Member.Service.WithdrewService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name = "탈퇴 기능", description = "탈퇴 기능 API")
@RestController
@Slf4j
@RequiredArgsConstructor
public class WithdrewController {

    private final WithdrewService withdrewService;
    private final MemberService memberService;

    /**
     * PathVariable을 이용한 회원 탈퇴 uri.
     * 로그인 된 사용자인지, 해당 사용자의 탈퇴 요청이 맞는지 확인 후 탈퇴 처리.
     * @param memberId
     * @param request
     * @return 로그인 되지 않은 상태면 403(forbidden), 다른 id 값을 통한 잘못된 요청을 하면 401(Unauthorized), 올바른 탈퇴 요청이면 200(OK)
     */

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자가 자신의 계정을 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete/{member_id}")
    @Operation(summary = "회원 탈퇴 (PathVariable)", description = "로그인한 사용자가 자신의 계정을 탈퇴합니다.")
    public ResponseEntity<String> withdrewRemoa(@PathVariable("member_id") Long memberId,
                                                @AuthenticationPrincipal MemberDetails memberDetails) {

        //PathVariable의 id와 로그인된 사용자의 id가 같은지 확인하기 위한 용도.
        if (!Objects.equals(memberId, memberDetails.getMemberId())) {
            return new ResponseEntity<>("회원정보가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        //해당 객체로 탈퇴 수행해야 dirty checking 통한 soft delete 적용 가능.
        Member member = memberService.findOne(memberId);

        withdrewService.withdrewRemoa(member);

        return new ResponseEntity<>("회원 탈퇴가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * Session 정보만을 활용해서 PathVariable 없이 구현한 회원 탈퇴 uri. 로그인 된 사용자인지만 확인하면 됨.
     *
     * @return 로그인 되지 않은 상태면 403(forbidden), 올바른 탈퇴 요청이면 200(OK)
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자가 자신의 계정을 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자가 자신의 계정을 탈퇴합니다.")
    public ResponseEntity<String> withdrewRemoaWithoutPathVariable(@AuthenticationPrincipal MemberDetails memberDetails) {

        Member findMember = memberService.findOne(memberDetails.getMemberId());

        withdrewService.withdrewRemoa(findMember);

        return new ResponseEntity<>("회원 탈퇴가 완료되었습니다.", HttpStatus.OK);
    }

}
