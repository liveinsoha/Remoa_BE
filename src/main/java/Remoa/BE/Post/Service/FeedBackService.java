package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Feedback;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Repository.FeedbackRepository;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedBackService {
    private final FeedbackRepository feedbackRepository;
    private final PostService postService;



    @Transactional
    public void registFeedback(Member member, String feedback, Long postId, Integer pageNumber){

        Feedback feedbackObj = new Feedback();
        Post post = postService.findOne(postId);

        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        feedbackObj.setPost(post);
        feedbackObj.setMember(member);
        feedbackObj.setPageNumber(pageNumber);
        feedbackObj.setFeedback(feedback);
        feedbackObj.setFeedbackTime(formatDate);

        feedbackRepository.saveFeedback(feedbackObj);
    }
}