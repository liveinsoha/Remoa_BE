package Remoa.BE.Web.ADMIN.Controller;


import Remoa.BE.Web.ADMIN.Service.AdminService;
import Remoa.BE.exception.response.ErrorResponse;
import Remoa.BE.utill.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 기능", description = "어드민 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;


    // 포스트 삭제
    @DeleteMapping("/post/{postId}")
    @Operation(summary = "ADMIN 포스트 삭제", description = "ADMIN 포스트를 삭제합니다. " +
            "<br> 응답데이터 정의 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트를 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = MessageUtils.FORBIDDEN,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    // 코멘트 삭제
    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "ADMIN 코멘트 삭제", description = "ADMIN 코멘트를 삭제합니다." +
            "<br> 응답데이터 정의 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코멘트를 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = MessageUtils.FORBIDDEN,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // 코멘트 답글 삭제
    @DeleteMapping("/comment-reply/{commentReplyId}")
    @Operation(summary = "ADMIN 코멘트 대댓글 삭제", description = "ADMIN 코멘트 대댓글을 삭제합니다." +
            "<br> 응답데이터 정의 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코멘트 답글을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = MessageUtils.FORBIDDEN,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCommentReply(@PathVariable Long commentReplyId) {
        adminService.deleteCommentReply(commentReplyId);
        return ResponseEntity.ok().build();
    }

    // 피드백 삭제
    @DeleteMapping("/feedback/{feedbackId}")
    @Operation(summary = "ADMIN 피드백 삭제", description = "ADMIN 피드백을 삭제합니다." +
            "<br> 응답데이터 정의 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = MessageUtils.FORBIDDEN,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        adminService.deleteFeedback(feedbackId);
        return ResponseEntity.ok().build();
    }

    // 피드백 답글 삭제
    @DeleteMapping("/feedback-reply/{feedbackReplyId}")
    @Operation(summary = "ADMIN 피드백 대댓글 삭제", description = "ADMIN 피드백 대댓글을 삭제합니다." +
            "<br> 응답데이터 정의 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 답글을 성공적으로 삭제했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = MessageUtils.FORBIDDEN,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteFeedbackReply(@PathVariable Long feedbackReplyId) {
        adminService.deleteFeedbackReply(feedbackReplyId);
        return ResponseEntity.ok().build();
    }

}
