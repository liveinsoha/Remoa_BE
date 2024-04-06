package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Domain.CommentFeedback;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Member.Service.FollowService;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.PostScarp;
import Remoa.BE.Post.Dto.Response.*;
import Remoa.BE.Post.Service.CommentFeedbackService;
import Remoa.BE.Post.Service.PostService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Remoa.BE.Member.Domain.ContentType.COMMENT;
import static Remoa.BE.Member.Domain.ContentType.FEEDBACK;

@Tag(name = "내 활동 기능", description = "내 활동 기능 API")
@RestController
@Slf4j
@RequiredArgsConstructor
public class MyActivityController {

    private final MemberService memberService;
    private final CommentFeedbackService commentFeedbackService;
    private final PostService postService;
    private final FollowService followService;

    /**
     * 내 활동 관리
     *
     * @param
     * @return Map<String, Object>
     * "contents" : 내가 작성한 최신 댓글(Comment, Feedback 무관 1개)
     * "posts" : 내가 스크랩한 post들을 가장 최근 스크랩한 순서의 List(12개).
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 활동을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/activity")
    @Operation(summary = "내 활동 조회", description = "내가 작성한 최신 댓글(Comment, Feedback 무관 1개)와 스크랩한 게시물들을 조회합니다.")
    public ResponseEntity<BaseResponse<ResMyActivityDto>> myActivity(@AuthenticationPrincipal MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);

        ResMyActivityDto result = new ResMyActivityDto();

        CommentFeedback commentFeedback = commentFeedbackService.findNewestCommentFeedback(myMember);

        ResCommentFeedbackDto commentOrFeedback = null;
        if (commentFeedback != null && commentFeedback.getType().equals(FEEDBACK)) {
            commentOrFeedback = feedbackBuilder(commentFeedback);
        } else if (commentFeedback != null && commentFeedback.getType().equals(COMMENT)) {
            commentOrFeedback = commentBuilder(commentFeedback);
        }
        result.setContent(commentOrFeedback);

        /**
         * 조회한 최근에 스크랩한 12개의 post들을 dto로 mapping.
         */
        List<ResPostDto> posts = postService.findRecentTwelveScrapedPost(myMember).stream()
                .map(post -> ResPostDto.builder()
                        .postId(post.getPostId())
                        .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                                post.getMember().getNickname(),
                                post.getMember().getProfileImage(),
                                followService.isMyMemberFollowMember(myMember, post.getMember())))
                        .thumbnail(post.getThumbnail().getStoreFileUrl())
                        .title(post.getTitle())
                        .likeCount(post.getLikeCount())
                        .isLikedPost((myMember != null && !post.getMember().getMemberId().equals(myMember.getMemberId())) ? postService.isThisPostLiked(myMember, post) : null)
                        .postingTime(post.getPostingTime().toString())
                        .views(post.getViews())
                        .scrapCount(post.getScrapCount())
                        .isScrapedPost((myMember != null && !post.getMember().getMemberId().equals(myMember.getMemberId())) ? postService.isThisPostScraped(myMember, post) : null)
                        .categoryName(post.getCategory().getName())
                        .build()).collect(Collectors.toList());
        result.setPosts(posts);

        BaseResponse<ResMyActivityDto> response = new BaseResponse<>(CustomMessage.OK, result);
        return ResponseEntity.ok(response);
        //return successResponse(CustomMessage.OK, result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 스크랩한 게시글을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "페이지 번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/scrap") // 내가 스크랩한 게시글 확인
    @Operation(summary = "내가 스크랩한 게시글 조회", description = "내가 스크랩한 게시글들을 확인합니다.")
    public ResponseEntity<BaseResponse<ResMyScrapDto>> myScrap(HttpServletRequest request,
                                                               @RequestParam(name = "page", defaultValue = "1", required = false) int pageNum,
                                                               @AuthenticationPrincipal MemberDetails memberDetails
    ) {

        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);


        ResMyScrapDto resMyScrapDto = new ResMyScrapDto();

        pageNum -= 1;
        if (pageNum < 0) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        /**
         * 조회한 최근에 스크랩한 12개의 post들을 dto로 mapping.
         */
        Page<PostScarp> posts = postService.findScrapedPost(pageNum, myMember);

        //조회할 레퍼런스가 db에 있으나, 현재 페이지에 조회할 데이터가 없는 경우 == 페이지 번호를 잘못 입력
        if ((posts.getContent().isEmpty()) && (posts.getTotalElements() > 0)) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //  return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        List<ResPostDto> postDtoList = posts.stream()
                .map(PostScarp::getPost)
                .collect(Collectors.toList())
                .stream()
                .map(post -> ResPostDto.builder()
                        .postId(post.getPostId())
                        .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                                post.getMember().getNickname(),
                                post.getMember().getProfileImage(),
                                followService.isMyMemberFollowMember(myMember, post.getMember())))
                        .thumbnail(post.getThumbnail().getStoreFileUrl())
                        .title(post.getTitle())
                        .likeCount(post.getLikeCount())
                        .isLikedPost((myMember != null && !post.getMember().getMemberId().equals(myMember.getMemberId())) ? postService.isThisPostLiked(myMember, post) : null)
                        .postingTime(post.getPostingTime().toString())
                        .views(post.getViews())
                        .scrapCount(post.getScrapCount())
                        .isScrapedPost((myMember != null && !post.getMember().getMemberId().equals(myMember.getMemberId())) ? postService.isThisPostScraped(myMember, post) : null)
                        .categoryName(post.getCategory().getName()).build())
                .collect(Collectors.toList());

        ResMyScrapDto myScrapDto = ResMyScrapDto.builder()
                .posts(postDtoList)
                .totalPages(posts.getTotalPages())  //전체 페이지의 수
                .totalOfAllPosts(posts.getTotalElements())//모든 코멘트의 수
                .totalOfPageElements(posts.getNumberOfElements())//현 페이지 피드백의 수
                .build();

        BaseResponse<ResMyScrapDto> response = new BaseResponse<>(CustomMessage.OK, myScrapDto);
        return ResponseEntity.ok(response);
        //  return successResponse(CustomMessage.OK, result);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 작성한 댓글을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "페이지 번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/comment")
    @Operation(summary = "내가 작성한 댓글 조회", description = "내가 작성한 최신 댓글들을 조회합니다.")
    public ResponseEntity<BaseResponse<ResMyCommentDto>> myComment(HttpServletRequest request,
                                                                   @RequestParam(name = "page", defaultValue = "1", required = false) int pageNum,
                                                                   @AuthenticationPrincipal MemberDetails memberDetails) {
        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);

        pageNum -= 1;
        if (pageNum < 0) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //  return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        Map<String, Object> result = new HashMap<>();

        Page<CommentFeedback> commentOrFeedback = commentFeedbackService.findNewestCommentOrFeedback(pageNum, myMember);

        //조회할 레퍼런스가 db에 있으나, 현재 페이지에 조회할 데이터가 없는 경우 == 페이지 번호를 잘못 입력
        if ((commentOrFeedback.getContent().isEmpty()) && (commentOrFeedback.getTotalElements() > 0)) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //  return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        /**
         * 조회한 가장 최근에 작성한 댓글들을 dto로 mapping
         */
        List<ResCommentFeedbackDto> contents = commentOrFeedback.stream().map(commentFeedback -> {
            ResCommentFeedbackDto map = null;
            if (commentFeedback.getType().equals(FEEDBACK)) {
                map = feedbackBuilder(commentFeedback);
            } else if (commentFeedback.getType().equals(COMMENT)) {
                map = commentBuilder(commentFeedback);
            }
            return map;
        }).collect(Collectors.toList());

        ResMyCommentDto myCommentDto = ResMyCommentDto.builder()
                .contents(contents)
                .totalPages(commentOrFeedback.getTotalPages())
                .totalOfAllComments(commentOrFeedback.getTotalElements())
                .totalOfPageElements(commentOrFeedback.getNumberOfElements())
                .build();

        BaseResponse<ResMyCommentDto> response = new BaseResponse<>(CustomMessage.OK, myCommentDto);
        return ResponseEntity.ok(response);
        //  return successResponse(CustomMessage.OK, result);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 받은 댓글/피드백을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "페이지 번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = MessageUtils.UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/receive")
    @Operation(summary = "내가 받은 댓글/피드백 조회", description = "내가 받은 최신 댓글/피드백들을 조회합니다.")
    public ResponseEntity<BaseResponse<ResReceivedCommentFeedbackDto>> receivedCommentFeedback(HttpServletRequest request,
                                                                                               @RequestParam(required = false, defaultValue = "all") String category,
                                                                                               @RequestParam(required = false, defaultValue = "1", name = "page") int pageNum,
                                                                                               @AuthenticationPrincipal MemberDetails memberDetails) {
        Long memberId = memberDetails.getMemberId();
        Member myMember = memberService.findOne(memberId);

        pageNum -= 1;
        if (pageNum < 0) {
            throw new BaseException(CustomMessage.PAGE_NUM_OVER);
            //    return errorResponse(CustomMessage.PAGE_NUM_OVER);
        }

        Page<CommentFeedback> commentOrFeedbacks = commentFeedbackService.findReceivedCommentOrFeedback(myMember, pageNum, category);

        List<ResCommentFeedbackDto> contents = commentOrFeedbacks
                .stream()
                .map(commentFeedback -> {
                    ResCommentFeedbackDto map = null;
                    if (commentFeedback.getType().equals(FEEDBACK)) {
                        map = feedbackBuilder(commentFeedback);
                    } else if (commentFeedback.getType().equals(COMMENT)) {
                        map = commentBuilder(commentFeedback);
                    }
                    return map;
                }).collect(Collectors.toList());


        ResReceivedCommentFeedbackDto responseDto = ResReceivedCommentFeedbackDto.builder()
                .contents(contents)
                .totalPages(commentOrFeedbacks.getTotalPages())
                .totalOfAllComments(commentOrFeedbacks.getTotalElements())
                .totalOfPageElements(commentOrFeedbacks.getNumberOfElements())
                .build();

        BaseResponse<ResReceivedCommentFeedbackDto> response = new BaseResponse<>(CustomMessage.OK, responseDto);
        return ResponseEntity.ok(response);
        // return successResponse(CustomMessage.OK, result);
    }


    private ResCommentFeedbackDto commentBuilder(CommentFeedback commentFeedback) {
        return ResCommentFeedbackDto.builder()
                .title(commentFeedback.getPost().getTitle())
                .postId(commentFeedback.getPost().getPostId())
                .commentId(commentFeedback.getComment().getCommentId())
                .thumbnail(commentFeedback.getPost().getThumbnail().getStoreFileUrl())
                .member(new ResMemberInfoDto(commentFeedback.getMember().getMemberId(),
                        commentFeedback.getMember().getNickname(),
                        commentFeedback.getMember().getProfileImage(),
                        null))
                .content(commentFeedback.getComment().getComment())
                .likeCount(commentFeedback.getComment().getCommentLikeCount()).build();
    }

    private ResCommentFeedbackDto feedbackBuilder(CommentFeedback commentFeedback) {
        return ResCommentFeedbackDto.builder()
                .title(commentFeedback.getPost().getTitle())
                .postId(commentFeedback.getPost().getPostId())
                .feedbackId(commentFeedback.getFeedback().getFeedbackId())
                .thumbnail(commentFeedback.getPost().getThumbnail().getStoreFileUrl())
                .member(new ResMemberInfoDto(commentFeedback.getMember().getMemberId(),
                        commentFeedback.getMember().getNickname(),
                        commentFeedback.getMember().getProfileImage(),
                        null))
                .content(commentFeedback.getFeedback().getFeedback())
                .likeCount(commentFeedback.getFeedback().getFeedbackLikeCount()).build();
    }
}
