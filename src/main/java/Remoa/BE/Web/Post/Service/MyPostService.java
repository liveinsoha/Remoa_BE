package Remoa.BE.Web.Post.Service;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Post;
import Remoa.BE.Web.Post.Repository.CategoryRepository;
import Remoa.BE.Web.Post.Repository.MyReferenceRepository;
import Remoa.BE.Web.Post.Repository.PostPagingRepository;
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

    private final PostPagingRepository postPagingRepository;
    private final CategoryRepository categoryRepository;

    private final MyReferenceRepository  myReferenceRepository;

    private static final int PAGE_SIZE = 12;
    private static final int RECEIVED_COMMENT_PAGE_SIZE = 3;

    public Page<Post> sortAndPaginatePostsByMember(int pageNumber, String sort, Member myMember, String title) {
        Page<Post> posts;
        PageRequest pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        //switch문을 통해 각 옵션에 맞게 sorting
        switch (sort) {
            case "views":
                posts = postPagingRepository.findByMemberAndTitleContainingOrderByViewsDesc(pageable, myMember,title);
                break;
            case "likes":
                posts =  postPagingRepository.findByMemberAndTitleContainingOrderByLikeCountDesc(pageable, myMember,title);
                break;
            case "scrap":
                posts = postPagingRepository.findByMemberAndTitleContainingOrderByScrapCountDesc(pageable, myMember,title);
                break;
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                posts = postPagingRepository.findByMemberAndTitleContainingOrderByPostingTimeDesc(pageable, myMember,title);
                break;
        }
        return posts;
    }

    public Page<Post> sortAndPaginatePostsByCategoryAndMember(String category, int pageNumber, String sort, Member myMember,String title) {
        Page<Post> posts;
        PageRequest pageable;
        switch (sort) {
            case "views":
                pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("views").descending());
                break;
            case "likes":
                pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("likeCount").descending());
                break;
            case "scrap":
                pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("scrapCount").descending());
                break;
            default:
                //sort 문자열이 잘못됐을 경우 default인 최신순으로 정렬
                pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("postingTime").descending());
                break;
        }
        posts =  postPagingRepository.findByMemberAndCategoryAndTitleContaining(pageable, myMember, categoryRepository.findByCategoryName(category),title);
        return posts;
    }

    /**
     * 받은 피드백 관리에서 쓰이는 최신 3개순 포스트
     * @param page
     * @param member
     * @param category
     * @return member가 작성한 최신 3개의 Post.
     */
    public Page<Post> getNewestThreePostsSortCategory(int page, Member member, String category) {
        PageRequest pageable = PageRequest.of(page, RECEIVED_COMMENT_PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository
                .findByMemberAndCategoryAndCommentsIsNotEmpty(pageable, member, categoryRepository.findByCategoryName(category));
    }

    /**
     * 받은 피드백 관리에서 쓰이는 최신 3개순 포스트
     * @param page
     * @param member
     * @return member가 작성한 최신 3개의 Post.
     */
    public Page<Post> getNewestThreePosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, RECEIVED_COMMENT_PAGE_SIZE, Sort.by("postingTime").descending());
        return postPagingRepository.findByMemberAndCommentsIsNotEmpty(pageable, member);
    }

    /**
     * 내 활동 관리에 쓰이는 코멘트 및 피드백을 단 작업물
     * @param size
     * @param member
     * @return Post
     */
    public Page<Post> getCommentedPost(int size, Member member) {
        PageRequest pageable = PageRequest.of(0, size, Sort.by("postingTime").descending());
        return postPagingRepository.findByMemberAndCommentsIsNotEmpty(pageable, member);
    }

    @Transactional
    public void deleteReferenceCategory(Long memberId, Long categoryId){
        try {
            List<Post> postList = myReferenceRepository.findByMemberMemberIdAndCategoryCategoryId(memberId,categoryId);
            myReferenceRepository.deleteAll(postList);
        }
        catch (Exception e)
        {
            throw e;
        }

    }


}