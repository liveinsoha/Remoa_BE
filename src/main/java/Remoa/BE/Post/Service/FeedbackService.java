package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.Feedback;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final PostService postService;
    @Transactional
    public Feedback findOne(Long feedbackId){
        Optional<Feedback> feedback = feedbackRepository.findOne(feedbackId);
        return feedback.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback not found"));
    }

    @Transactional
    public void registerFeedback(Member member, String feedback, Long postId, Integer pageNumber, Long feedbackId){

        Feedback parentFeedback = null;
        if (feedbackId != null) {
            parentFeedback = feedbackRepository.findByFeedbackId(feedbackId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback not found"));
        }


        Feedback feedbackObj = new Feedback();
        Post post = postService.findOne(postId);

        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        feedbackObj.setPost(post);
        feedbackObj.setMember(member);
        feedbackObj.setParentFeedback(parentFeedback); //대댓글인 경우 원 댓글의 Feedback, 댓글인 경우 null
        feedbackObj.setPageNumber(pageNumber); //대댓글인 경우 null. parentFeedback.getPageNumber()통헤서 값 넣어도 됩니다.
        feedbackObj.setFeedback(feedback);
        feedbackObj.setFeedbackLikeCount(0);
        feedbackObj.setFeedbackTime(formatDate);

        feedbackRepository.saveFeedback(feedbackObj);
    }

    @Transactional
    public void modifyFeedback(String feedback, Long feedbackId){
        Feedback feedbackObj = findOne(feedbackId);
        feedbackObj.setFeedback(feedback);
        feedbackRepository.updateFeedback(feedbackObj);
    }

}