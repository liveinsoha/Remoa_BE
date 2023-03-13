package Remoa.BE.Post.Controller;

import Remoa.BE.Post.Dto.Response.ThumbnailReferenceDto;
import Remoa.BE.Post.Repository.SearchRepository;
import Remoa.BE.Post.Service.SearchService;
import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static Remoa.BE.exception.CustomBody.successResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {
    private SearchService searchService;

    @GetMapping("/reference")
    public ResponseEntity<Object> searchPost(@RequestParam String name) {
        return successResponse(CustomMessage.OK, searchService.searchPost(name));
    }
}