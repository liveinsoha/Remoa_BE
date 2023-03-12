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
    private  final PostRepository postRepository;



    @Transactional
    public void registFeedback(Member member, String feedback, Long postId, Integer pageNumber){

        Feedback feedbackObj = new Feedback();
        Optional<Post> post = postRepository.findOne(postId);
        Post myPost; // feedback을 등록할 게시물
        if(post.isPresent()){
            myPost = post.get();
        } else{
            myPost = post.orElseThrow(() -> new IllegalStateException("값이 없습니다."));
        }


        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        feedbackObj.setPost(myPost);
        feedbackObj.setMember(member);
        feedbackObj.setPageNumber(pageNumber);
        feedbackObj.setFeedback(feedback);
        feedbackObj.setFeedbackTime(formatDate);

        feedbackRepository.saveFeedback(feedbackObj);
    }
}