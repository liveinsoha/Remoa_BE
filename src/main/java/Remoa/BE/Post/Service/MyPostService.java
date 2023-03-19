package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.CategoryRepository;
import Remoa.BE.Post.Repository.PostPagingRepository;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPostService {

    private final PostRepository postRepository;
    private final PostPagingRepository postPagingRepository;
    private final CategoryRepository categoryRepository;
    private static final int PAGE_SIZE = 12;

    /**
     * 특정 member의 post들 전체 조회
     * @param member
     * @return
     */
    public List<Post> showOnesPosts(Member member) {
        return postRepository.findByMember(member);
    }

    /**
     * paging : 특정 member의 post들 12개씩 조회
     * sorting : 최신순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getNewestPosts(int page, Member member) {
//        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("postingTime").descending());
//        return postPagingRepository.findAllByMember(pageable, member);

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);
        return postPagingRepository.findAllByMemberOrderByPostingTimeDesc(pageable, member);
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 과거순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getOldestPosts(int page, Member member) {
//        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("postingTime").ascending());
//        return postPagingRepository.findAllByMember(pageable, member);

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);
        return postPagingRepository.findAllByMemberOrderByPostingTimeAsc(pageable, member);
    }

    public Page<Post> sortAndPaginatePostsByMember(int pageNumber, String sort, Member myMember) {
        Page<Post> posts;
        //switch문을 통해 각 옵션에 맞게 sorting
        switch (sort) {
            case "newest":
                posts = getNewestPosts(pageNumber, myMember);
                break;
            case "oldest":
                posts = getOldestPosts(pageNumber, myMember);
                break;
            case "like":
                posts = getMostLikedPosts(pageNumber, myMember);
                break;
            case "scrap":
                posts = getMostScrapedPosts(pageNumber, myMember);
                break;
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                posts = getNewestPosts(pageNumber, myMember);
                break;
        }
        return posts;
    }

    public Page<Post> sortAndPaginatePostsByCategoryAndMember(String category, int pageNumber, String sort, Member myMember) {
        Page<Post> posts;
        switch (sort) {
            case "newest":
                posts = getNewestPostsSortCategory(pageNumber, myMember, category);
                break;
            case "oldest":
                posts = getOldestPostsSortCategory(pageNumber, myMember, category);
                break;
            case "like":
                posts = getMostLikedPostsSortCategory(pageNumber, myMember, category);
                break;
            case "scrap":
                posts = getMostScrapedPostsSortCategory(pageNumber, myMember, category);
                break;
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                posts = getNewestPostsSortCategory(pageNumber, myMember, category);
                break;
        }
        return posts;
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 좋아요 순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getMostLikedPosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);
        return postPagingRepository.findAllByMemberOrderByLikeCountDesc(pageable, member);
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 스크랩 순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getMostScrapedPosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);
        return postPagingRepository.findAllByMemberOrderByScrapCountDesc(pageable, member);
    }

    public Page<Post> getNewestPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository.findAllByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> getOldestPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("postingTime").ascending());
        return postPagingRepository.findAllByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> getMostLikedPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("likeCount").descending());
        return postPagingRepository.findAllByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> getMostScrapedPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("scrapCount").descending());
        return postPagingRepository.findAllByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

}