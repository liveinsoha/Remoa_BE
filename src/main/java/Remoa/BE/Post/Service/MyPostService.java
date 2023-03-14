package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.PostPagingRepository;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPostService {

    private final PostRepository postRepository;
    private final PostPagingRepository postPagingRepository;
    private static final int pageSize = 5;

    /**
     * 특정 member의 post들 전체 조회
     * @param member
     * @return
     */
    public List<Post> showOnesPosts(Member member) {
        return postRepository.findByMember(member);
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 최신순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getNewestPosts(int page, Member member) {
//        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("postingTime").descending());
//        return postPagingRepository.findAllByMember(pageable, member);

        PageRequest pageable = PageRequest.of(page, pageSize);
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

        PageRequest pageable = PageRequest.of(page, pageSize);
        return postPagingRepository.findAllByMemberOrderByPostingTimeAsc(pageable, member);
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 좋아요 순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getMostLikePosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        return postPagingRepository.findAllByMemberOrderByLikeCountDesc(pageable, member);
    }

    /**
     * paging : 특정 member의 post들 5개씩 조회
     * sorting : 스크랩 순으로 정렬
     * @param page : 조회하려는 페이지 번호
     * @param member : 조회하려는 작성자
     * @return Page<Post>
     */
    public Page<Post> getMostScrapPosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        return postPagingRepository.findAllByMemberOrderByScrapCountDesc(pageable, member);
    }

}