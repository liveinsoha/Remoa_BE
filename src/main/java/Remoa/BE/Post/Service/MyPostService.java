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
    final int pageSize = 3;

    /**
     * 특정 member의 post들 전체 조회
     * @param member
     * @return
     */
    public List<Post> showOnesPosts(Member member) {
        return postRepository.findByMember(member);
    }

    /**
     * 특정 member의 post들 5개씩 최신순으로 조회
     * pageSize를 수정해서 한 페이지에 보여질 post의 개수를 정할 수 있음.
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

    public Page<Post> getOldestPosts(int page, Member member) {
//        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("postingTime").ascending());
//        return postPagingRepository.findAllByMember(pageable, member);

        PageRequest pageable = PageRequest.of(page, pageSize);
        return postPagingRepository.findAllByMemberOrderByPostingTimeAsc(pageable, member);
    }

    public Page<Post> getMostLikePosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        return postPagingRepository.findAllByMemberOrderByLikeCountDesc(pageable, member);
    }

    public Page<Post> getMostScrapPosts(int page, Member member) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        return postPagingRepository.findAllByMemberOrderByScrapCountDesc(pageable, member);
    }

}