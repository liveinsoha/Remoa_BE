package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.PostScarp;
import Remoa.BE.Post.Dto.Request.UploadPostForm;
import Remoa.BE.Post.Repository.CategoryRepository;
import Remoa.BE.Post.Repository.PostPagingRepository;
import Remoa.BE.Post.Repository.PostRepository;
import Remoa.BE.Post.Repository.PostScrapRepository;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.utill.FileExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Remoa.BE.exception.CustomBody.successResponse;
import static Remoa.BE.utill.FileExtension.fileExtension;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {


    private final MemberService memberService;

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final FileService fileService;

    private final PostPagingRepository postPagingRepository;

    private final PostScrapRepository postScrapRepository;

    private static final int HOME_PAGE_SIZE = 12;

    public Post findOne(Long postId) {
        Optional<Post> post = postRepository.findOne(postId);
        return post.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found"));
    }

    @Transactional
    public Post registerPost(UploadPostForm uploadPostForm,MultipartFile thumbnail ,List<MultipartFile> uploadFiles, Long memberId) throws IOException {

        Category category = categoryRepository.findByCategoryName(uploadPostForm.getCategory());

        Member member = memberService.findOne(memberId);

//        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //확장자 확인
        String extension = fileExtension(uploadFiles.get(0));
        if(extension.equals("pdf") || extension.equals("jpg") || extension.equals("png")){
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
                    .postingTime(LocalDateTime.now())
                    .likeCount(0)
                    .views(0)
                    .scrapCount(0)
                    .deleted(false)
                    .build();

            fileService.saveUploadFiles(post, thumbnail ,uploadFiles);
            return post;
        }
        else{
            throw new IOException();
        }


    }

    public Page<Post> sortAndPaginatePosts(String sort, int pageNumber) {
        Page<Post> allPosts;
        switch (sort) {
            case "newest":
                allPosts = findAllPostsWithPaginationForHomepageNewest(pageNumber);
                break;
            case "like":
                allPosts = findAllPostsWithPaginationForHomepageMostLiked(pageNumber);
                break;
            case "scrap":
                allPosts = findAllPostsWithPaginationForHomepageMostScraped(pageNumber);
                break;
            case "view":
                allPosts = findAllPostsWithPaginationForHomepageMostViewed(pageNumber);
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                allPosts = findAllPostsWithPaginationForHomepageNewest(pageNumber);
                break;
        }
        return allPosts;
    }

    public Page<Post> sortAndPaginatePostsByCategory(String category, String sort, int pageNumber) {
        Page<Post> allPosts;
        switch (sort) {
            case "newest":
                allPosts = findAllPostsWithPaginationForHomepageSortByCategoryNewest(pageNumber, category);
                break;
            case "like":
                allPosts = findAllPostsWithPaginationForHomepageSortByCategoryMostLiked(pageNumber, category);
                break;
            case "scrap":
                allPosts = findAllPostsWithPaginationForHomepageSortByCategoryMostScraped(pageNumber, category);
                break;
            case "view":
                allPosts = findAllPostsWithPaginationForHomepageSortByCategoryMostViewed(pageNumber, category);
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                allPosts = findAllPostsWithPaginationForHomepageSortByCategoryNewest(pageNumber, category);
                break;
        }
        return allPosts;
    }

    public List<Post> searchPost(String name) {
        return postRepository.findByTitleContaining(name);
    }

    public List<Post> findAll(){
        return postRepository.findAll();
    }

    public Page<Post> findAllPostsWithPaginationForHomepageNewest(int page) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository.findAll(pageable);
    }

    public Page<Post> findAllPostsWithPaginationForHomepageMostViewed(int page) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("views").descending());
        return postPagingRepository.findAll(pageable);
    }

    public Page<Post> findAllPostsWithPaginationForHomepageMostLiked(int page) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("likeCount").descending());
        return postPagingRepository.findAll(pageable);
    }

    public Page<Post> findAllPostsWithPaginationForHomepageMostScraped(int page) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("scrapCount").descending());
        return postPagingRepository.findAll(pageable);
    }

    public Page<Post> findAllPostsWithPaginationForHomepageSortByCategoryNewest(int page, String category) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository.findAllByCategory(pageable, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> findAllPostsWithPaginationForHomepageSortByCategoryMostViewed(int page, String category) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("views").descending());
        return postPagingRepository.findAllByCategory(pageable, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> findAllPostsWithPaginationForHomepageSortByCategoryMostLiked(int page, String category) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("likeCount").descending());
        return postPagingRepository.findAllByCategory(pageable, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> findAllPostsWithPaginationForHomepageSortByCategoryMostScraped(int page, String category) {
        Pageable pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("scrapCount").descending());
        return postPagingRepository.findAllByCategory(pageable, categoryRepository.findByCategoryName(category));
    }

    @Transactional
    public PostScarp getPostScrapByMemberIdAndPostId(Long memberId, Long postId){
        return postScrapRepository.findByMemberMemberIdAndPostPostId(memberId, postId);
    }

    @Transactional
    public void scrapPost(Long memberId, Member myMember, Long referenceId){
        Post post = findOne(referenceId);
        Integer postScrapCount = post.getScrapCount(); // 이 게시물을 스크랩한 수

        // scrapPost를 db에서 조회해보고 조회 결과가 null이면 scrapCount += 1, PostScrap 생성
        // null이 아니면 scrapCount -= 1, 조회결과인 해당 PostScrap 삭제
        PostScarp postScarp = getPostScrapByMemberIdAndPostId(memberId, referenceId);
        if(postScarp == null){
            post.setScrapCount(postScrapCount + 1); // 스크랩 수 1 증가
            PostScarp postScrapObj = PostScarp.createPostScrap(myMember, post);
            postScrapRepository.save(postScrapObj);
        } else{
            post.setScrapCount(post.getScrapCount() - 1); // 스크랩 수 1 차감
            postScrapRepository.deleteById(postScarp.getPostScrapId()); // db에서 삭제
        }
    }
}
