package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Repository.PostRepository;
import Remoa.BE.Post.Repository.UploadFileRepository;
import Remoa.BE.Post.Repository.CategoryRepository;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Dto.Response.ResReferenceDto;
import Remoa.BE.Post.Dto.Response.ResRegistCommentDto;
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
public class PostService {

    private final UploadFileRepository uploadFileRepository;

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final FileService fileService;

    public Post findOne(Long postId) {
        Optional<Post> post = postRepository.findOne(postId);
        return post.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found"));
    }


    @Transactional
    public Post registerPost(UploadPostForm uploadPostForm, MultipartFile uploadFile, Member member) {

        Category category = categoryRepository.findByCategoryName(uploadPostForm.getCategory());

        Post post = Post.builder()
                .title(uploadPostForm.getTitle())
                .member(member)
                .contestName(uploadPostForm.getContestName())
                .category(category)
                .contestAwareType(uploadPostForm.getContestAwardType())
                .build();

        fileService.saveUploadFile(post, uploadFile);
        postRepository.savePost(post);

        return post;
    }



}
