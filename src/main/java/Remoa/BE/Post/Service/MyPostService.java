package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Member;
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
    private static final int MY_POST_PAGE_SIZE = 12;
    private static final int RECEIVED_FEEDBACK_PAGE_SIZE = 3;

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

        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE);
        return postPagingRepository.findByMemberOrderByPostingTimeDesc(pageable, member);
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 과거순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getMostViewedPosts(int page, Member member) {
//        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("postingTime").ascending());
//        return postPagingRepository.findAllByMember(pageable, member);

        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE);
        return postPagingRepository.findByMemberOrderByViewsDesc(pageable, member);
    }

    public Page<Post> sortAndPaginatePostsByMember(int pageNumber, String sort, Member myMember,String title) {
        Page<Post> posts;
        PageRequest pageable = PageRequest.of(pageNumber, MY_POST_PAGE_SIZE);
        //switch문을 통해 각 옵션에 맞게 sorting
        switch (sort) {
            case "view":
                posts = postPagingRepository.findByMemberOrderByViewsDesc(pageable, myMember);
                break;
            case "like":
                posts =  postPagingRepository.findByMemberOrderByLikeCountDesc(pageable, myMember);;
                break;
            case "scrap":
                posts = postPagingRepository.findByMemberOrderByScrapCountDesc(pageable, myMember);
                break;
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                posts = postPagingRepository.findByMemberOrderByPostingTimeDesc(pageable, myMember);
                break;
        }
        return posts;
    }

    public Page<Post> sortAndPaginatePostsByCategoryAndMember(String category, int pageNumber, String sort, Member myMember,String title) {
        Page<Post> posts;
        PageRequest pageable;
        switch (sort) {
            case "view":
                pageable = PageRequest.of(pageNumber, MY_POST_PAGE_SIZE, Sort.by("views").descending());
                break;
            case "like":
                pageable = PageRequest.of(pageNumber, MY_POST_PAGE_SIZE, Sort.by("likeCount").descending());
                break;
            case "scrap":
                pageable = PageRequest.of(pageNumber, MY_POST_PAGE_SIZE, Sort.by("scrapCount").descending());
                break;
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                pageable = PageRequest.of(pageNumber, MY_POST_PAGE_SIZE, Sort.by("postingTime").descending());
                break;
        }
        posts =  postPagingRepository.findByMemberAndCategoryAndTitleContaining(pageable, myMember, categoryRepository.findByCategoryName(category),title);
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
        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE);
        return postPagingRepository.findByMemberOrderByLikeCountDesc(pageable, member);
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 스크랩 순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getMostScrapedPosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE);
        return postPagingRepository.findByMemberOrderByScrapCountDesc(pageable, member);
    }

    public Page<Post> getNewestPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository.findByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> getMostViewedPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE, Sort.by("views").ascending());
        return postPagingRepository.findByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> getMostLikedPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE, Sort.by("likeCount").descending());
        return postPagingRepository.findByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

    public Page<Post> getMostScrapedPostsSortCategory(int page, Member member, String  category) {
        PageRequest pageable = PageRequest.of(page, MY_POST_PAGE_SIZE, Sort.by("scrapCount").descending());
        return postPagingRepository.findByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));
    }

    /**
     * 받은 피드백 관리에서 쓰이는 최신 3개순 포스트
     * @param page
     * @param member
     * @param category
     * @return member가 작성한 최신 3개의 Post.
     */
    public Page<Post> getNewestThreePostsSortCategory(int page, Member member, String category) {
        PageRequest pageable = PageRequest.of(page, RECEIVED_FEEDBACK_PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository.findByMemberAndCategory(pageable, member, categoryRepository.findByCategoryName(category));

    }

    /**
     * 받은 피드백 관리에서 쓰이는 최신 3개순 포스트
     * @param page
     * @param member
     * @return member가 작성한 최신 3개의 Post.
     */
    public Page<Post> getNewestThreePosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, RECEIVED_FEEDBACK_PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository.findByMember(pageable, member);

    }

}