package Remoa.BE.Post.Service;

import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Dto.Response.ThumbnailReferenceDto;
import Remoa.BE.Post.Repository.SearchRepository;
import Remoa.BE.exception.CustomMessage;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static Remoa.BE.exception.CustomBody.successResponse;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    // 게시글 검색
    public ResponseEntity<Object> searchPost(String name) {
        // 검색값이 포함되어 있는 게시글을 가져옴
        List<ThumbnailReferenceDto> searchPosts = searchRepository.findByTitleContaining(name);
        List<Post> searchList = new ArrayList<>();

        if (searchPosts.isEmpty()) {

            // 검색 결과가 없는 경우 모두 반환
            List<ThumbnailReferenceDto> allPosts = searchRepository.findAll();
            for (ThumbnailReferenceDto tr : allPosts) {
                searchList.add(Post.builder()
                        .postId(tr.getPostId())
                        //.nickname(tr.getNickname())
                        .title(tr.getTitle())
                        .likeCount(tr.getLikeCount())
                        .postingTime(tr.getPostingTime())
                        .views(tr.getViews())
                        .scrapCount(tr.getScrapCount())
                        //.storeFileUrls(tr.getStoreFileUrls())
                        //.categoryName(tr.getCategoryName())
                        .build()
                );
            }
        } else { // 검색 결과가 있는 경우
            for (ThumbnailReferenceDto tr : searchPosts) {
                searchList.add(Post.builder()
                        .postId(tr.getPostId())
                        //.nickname(tr.getNickname())
                        .title(tr.getTitle())
                        .likeCount(tr.getLikeCount())
                        .postingTime(tr.getPostingTime())
                        .views(tr.getViews())
                        .scrapCount(tr.getScrapCount())
                        //.storeFileUrls(tr.getStoreFileUrls())
                        //.categoryName(tr.getCategoryName())
                        .build()
                );
            }
        }

        return successResponse(CustomMessage.OK, searchList);
    }
}
