package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Comment;
import Remoa.BE.Member.Domain.CommentBookmark;
import Remoa.BE.Member.Domain.CommentLike;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.CommentRepository;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long writeComment(Comment comment) {
        commentRepository.saveComment(comment);
        return comment.getCommentId();
    }

    public List<Comment> loadCommentsByPostId(Long postId) {
        Post post = postRepository.findByPostId(postId);
        return commentRepository.findByPost(post);
    }

    @Transactional
    public Long commentLikeAction(Comment comment, Member member) {
        CommentLike commentLike = CommentLike.createCommentLike(member, comment);
        commentRepository.saveCommentLike(commentLike);
        return commentLike.getCommentLikeId();
    }

    @Transactional
    public Long commentBookmarkAction(Comment comment, Member member) {
        CommentBookmark commentBookmark = CommentBookmark.createCommentBookmark(member, comment);
        commentRepository.saveCommentBookmark(commentBookmark);
        return commentBookmark.getCommentBookmarkId();
    }

    @Transactional
    public Post registerComment(Member member, String comment, Long postId){
        Comment commentObj = new Comment();
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //Post post = postRepository.findByPostId(postId); // 댓글을 작성하는 게시글
        Optional<Post> post = postRepository.findOne(postId);
        Post myPost; // 댓글을 등록할 게시물
        if(post.isPresent()){
            myPost = post.get();
        } else{
            myPost = post.orElseThrow(() -> new IllegalStateException("값이 없습니다."));
        }

        commentObj.setPost(myPost);
        commentObj.setMember(member);
        commentObj.setComment(comment);
        commentObj.setCommentedTime(formatDate);
        commentRepository.saveComment(commentObj);

        return post.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
    }
}