package Remoa.BE.Post.Service;

import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Response.ThumbnailReferenceDto;
import Remoa.BE.Post.Repository.SearchRepository;
import Remoa.BE.exception.CustomMessage;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static Remoa.BE.exception.CustomBody.successResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    // 게시글 검색
    public ResponseEntity<Object> searchPost(String name) {
        List<Post> searchPosts;

        if (name == null) {
            // 검색어가 없는 경우 전체 게시글을 가져옴
            searchPosts = searchRepository.findAll();
        } else {
            // 검색어가 포함되어 있는 게시글을 가져옴
            searchPosts = searchRepository.findByTitleContaining(name);
        }

        List<Post> searchList = new ArrayList<>();
        for (Post post : searchPosts) {
            searchList.add(Post.builder()
                    .postId(post.getPostId())
                    //.nickname(post.getNickname())
                    .title(post.getTitle())
                    .likeCount(post.getLikeCount())
                    .postingTime(post.getPostingTime())
                    .views(post.getViews())
                    .scrapCount(post.getScrapCount())
                    //.storeFileUrls(post.getStoreFileUrls())
                    //.categoryName(post.getCategoryName())
                    .build()
            );
        }
        return successResponse(CustomMessage.OK, searchList);
    }
}