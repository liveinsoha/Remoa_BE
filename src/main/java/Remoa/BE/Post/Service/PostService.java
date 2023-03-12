package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Repository.CategoryRepository;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {


    private final MemberService memberService;

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final FileService fileService;

    public Post findOne(Long postId) {
        Optional<Post> post = postRepository.findOne(postId);
        return post.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found"));
    }


    @Transactional
    public Post registerPost(UploadPostForm uploadPostForm,MultipartFile thumbnail ,List<MultipartFile> uploadFiles, Long memberId) throws IOException {

        Category category = categoryRepository.findByCategoryName(uploadPostForm.getCategory());

        Member member = memberService.findOne(memberId);

        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //확장자 확인
        String fileName = uploadFiles.get(0).getOriginalFilename();
        assert fileName != null;
        int lastIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(lastIndex + 1);
        if(extension.equals("pdf") || extension.equals("jpg")){
            int pageCount;
            if(extension.equals("pdf")){
                PDDocument document = PDDocument.load(uploadFiles.get(0).getInputStream());
                pageCount = document.getNumberOfPages();
            }
           else{
               pageCount = uploadFiles.size();
            }
            Post post = Post.builder()
                    .title(uploadPostForm.getTitle())
                    .member(member)
                    .contestName(uploadPostForm.getContestName())
                    .category(category)
                    .contestAwareType(uploadPostForm.getContestAwardType())
                    .pageCount(pageCount)
                    .postingTime(formatDate)
                    .deleted(false)
                    .build();

            fileService.saveUploadFiles(post, thumbnail ,uploadFiles);
            return post;
        }
        else{
            return null;
        }


    }

}
