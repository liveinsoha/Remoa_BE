package Remoa.BE.Web.CommentFeedback.Service;

import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.CommentFeedback.Domain.CommentFeedback;
import Remoa.BE.Web.Feedback.Domain.Feedback;
import Remoa.BE.Web.Post.Domain.Category;
import Remoa.BE.Web.Post.Domain.Post;
import Remoa.BE.Web.Post.Repository.CategoryRepository;
import Remoa.BE.Web.CommentFeedback.Repository.CommentFeedbackRepository;
import Remoa.BE.Web.Member.Domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFeedbackService {
    private static final int CONTENT_PAGE_SIZE = 4;
    private static final int RECEIVED_PAGE_SIZE = 4;
    private final CommentFeedbackRepository commentFeedbackRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CommentFeedback saveCommentFeedback(Comment comment, Feedback feedback, ContentType type,
                                               Member member, Post post, LocalDateTime time) {

        CommentFeedback commentFeedback = CommentFeedback.builder()
                .type(type)
                .member(member)
                .post(post)
                .comment(comment)
                .feedback(feedback)
                .time(time)
                .deleted(false).build();
        return commentFeedbackRepository.save(commentFeedback);
    }

    public Page<CommentFeedback> findNewestCommentOrFeedback(int page, Member member) {
        Pageable pageable = PageRequest.of(page, CONTENT_PAGE_SIZE);
        return commentFeedbackRepository.findByMemberOrderByTimeDesc(pageable, member);
    }

    public CommentFeedback findNewestCommentFeedback(Member member) {
        return commentFeedbackRepository.findByMemberOrderByTime(member).orElse(null);
    }

    public CommentFeedback findComment(Comment comment) {
        return commentFeedbackRepository.findByComment(comment)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));
    }

    public CommentFeedback findFeedback(Feedback feedback) {
        return commentFeedbackRepository.findByFeedback(feedback)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback not found"));
    }

    public Page<CommentFeedback> findReceivedCommentOrFeedback(Member myMember, int pageNum, String categoryString) {
        Category category = null;
        List<String> categoryList = Arrays.asList("idea", "marketing", "design", "video", "digital", "etc");
        if (categoryList.contains(categoryString)) {
            category = categoryRepository.findByCategoryName(categoryString);
        }
        PageRequest pageable = PageRequest.of(pageNum, RECEIVED_PAGE_SIZE);
        return commentFeedbackRepository.findRecentReceivedCommentFeedback(myMember, pageable, category);
    }

}
