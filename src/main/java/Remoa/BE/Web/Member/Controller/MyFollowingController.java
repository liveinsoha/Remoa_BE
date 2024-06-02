package Remoa.BE.Web.Member.Controller;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Dto.Res.ResMypageFollowing;
import Remoa.BE.Web.Member.Service.MemberService;
import Remoa.BE.Web.Member.Service.MyFollowingService;
import Remoa.BE.config.auth.MemberDetails;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "마이페이지 팔로잉 Test Completed", description = "마이페이지 팔로잉 기능 API")
@RestController
@Slf4j
@RequiredArgsConstructor
public class MyFollowingController {

    private final MemberService memberService;

    private final MyFollowingService myFollowingService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인된 사용자의 팔로잉 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/following") // 마이페이지 팔로잉 관리화면
    @Operation(summary = "마이페이지 팔로잉 관리화면 Test Completed", description = "현재 로그인된 사용자의 팔로잉 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<ResMypageFollowing>> mypageFollowing(@AuthenticationPrincipal MemberDetails memberDetails) {
        log.info("EndPoint Get /following");

        Long myMemberId = memberDetails.getMemberId();

        Member myMember = memberService.findOne(myMemberId);
        ResMypageFollowing resMypageFollowing = myFollowingService.mypageFollowing(myMember);

        BaseResponse<ResMypageFollowing> response = new BaseResponse<>(CustomMessage.OK, resMypageFollowing);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, resMypageFollowing);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인된 사용자의 팔로워 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/follower") // 마이페이지 팔로워 관리화면
    @Operation(summary = "마이페이지 팔로워 관리화면 Test Completed", description = "현재 로그인된 사용자의 팔로워 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<ResMypageFollowing>> mypageFollower(@AuthenticationPrincipal MemberDetails memberDetails) {
        log.info("EndPoint Get /follower");

        Long myMemberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(myMemberId);
        ResMypageFollowing resMypageFollower = myFollowingService.mypageFollower(myMember);
        BaseResponse<ResMypageFollowing> response = new BaseResponse<>(CustomMessage.OK, resMypageFollower);
        return ResponseEntity.ok(response);
        //   return successResponse(CustomMessage.OK, resMypageFollower);

    }

}
