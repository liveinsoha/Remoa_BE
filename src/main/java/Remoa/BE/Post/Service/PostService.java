package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Repository.PostRepository;
import Remoa.BE.Post.Repository.UploadFileRepository;
import Remoa.BE.Post.Repository.CategoryRepository;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final UploadFileRepository uploadFileRepository;

    private final MemberService memberService;

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final FileService fileService;

    public Post findOne(Long postId) {
        Optional<Post> post = postRepository.findOne(postId);
        return post.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found"));
    }


    @Transactional
    public Post registerPost(UploadPostForm uploadPostForm, List<MultipartFile> uploadFiles, Long memberId) {

        Category category = categoryRepository.findByCategoryName(uploadPostForm.getCategory());

        Member member = memberService.findOne(memberId);

        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Post post = Post.builder()
                .title(uploadPostForm.getTitle())
                .member(member)
                .contestName(uploadPostForm.getContestName())
                .category(category)
                .contestAwareType(uploadPostForm.getContestAwardType())
                .likeCount(0)
                .scrapCount(0)
                .views(0)
                .postingTime(formatDate)
                .deleted(false)
                .build();

        fileService.saveUploadFiles(post, uploadFiles);

        return post;
    }

}
