package Remoa.BE.Post.Controller;

import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static Remoa.BE.utill.MemberInfo.getMemberId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final FileService fileService;

    //Todo 게시글 작성중 파일 업로드만 작성
    @PostMapping("/post")
    public void posting(Post post, @RequestParam("files") List<MultipartFile> multipartFile){
        fileService.saveUploadFiles(post,multipartFile);
    }

    /**
     * @param fileId file PK
     * @return file이 저장된 url 반환
     */
    @GetMapping("/post/file/{fileId}/url")
    public String getFileUrl(@PathVariable("fileId") Long fileId ){
        return fileService.getUrl(fileId);
    }

    /**
     * @param fileId file PK
     * @return file을 바로 다운로드 할 수 있다
     */
    @GetMapping("/post/file/{fileId}")
    public ResponseEntity<byte[]> getFileDownload(@PathVariable("fileId") Long fileId ) throws IOException {
        return fileService.getObject(fileId);
    }

}
