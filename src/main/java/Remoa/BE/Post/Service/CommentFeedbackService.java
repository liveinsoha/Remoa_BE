package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.*;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.CommentFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFeedbackService {
    private static final int CONTENT_PAGE_SIZE = 4;
    private final CommentFeedbackRepository commentFeedbackRepository;

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
        return commentFeedbackRepository.findByMemberOrderByTime(member);
    }

}
