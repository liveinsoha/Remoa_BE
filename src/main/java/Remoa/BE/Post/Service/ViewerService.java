package Remoa.BE.Post.Service;


import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewerService {

    private final PostService postService;

    @Transactional
    public Post getPost(Long postId) {
        // 게시물 정보 검색
        Post post = postService.findOne(postId);

        // 조회수 업데이트
        post.setViews(post.getViews() + 1);

        return post;
    }

    @Transactional
    public void updateScrapCount(Long postId) {
        // 게시물 정보 검색
        Post post = postService.findOne(postId);

        // 스크랩수 업데이트
        int scrapCount = post.getPostScarps().size();
        post.setScrapCount(scrapCount);
    }
}