package Remoa.BE.Post.Controller;

import Remoa.BE.Member.Dto.Res.ResMemberInfoDto;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Response.ResReferenceViewerDto;
import Remoa.BE.Post.Service.ViewerService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static Remoa.BE.exception.CustomBody.successResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ViewerController {

    private final ViewerService viewerService;

    @GetMapping("reference/{reference_id}")
    public ResponseEntity<Object> referenceViewer(HttpServletRequest request,
                                                  @PathVariable("reference_id") Long referenceId) {

        Post post = viewerService.getPost(referenceId);

        ResReferenceViewerDto result = ResReferenceViewerDto.builder()
                .postingTime(post.getPostingTime().toString())
                .postMember(new ResMemberInfoDto(post.getMember().getMemberId(),
                        post.getMember().getNickname(),
                        post.getMember().getProfileImage()))
                .postId(post.getPostId())
                .views(post.getViews())
                .category(post.getCategory().getName())
                .likeCount(post.getLikeCount())
                .thumbnail(post.getThumbnail().getStoreFileUrl())
                .scrapCount(post.getScrapCount())
                .title(post.getTitle())
                .build();

        return successResponse(CustomMessage.OK, result);
    }
}