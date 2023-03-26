package Remoa.BE.Post.Service;

import Remoa.BE.Member.Domain.*;
import Remoa.BE.Member.Service.MemberService;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import Remoa.BE.Post.Repository.FeedbackLikeRepository;
import Remoa.BE.Post.Repository.FeedbackRepository;
import Remoa.BE.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static Remoa.BE.Member.Domain.ContentType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackLikeRepository feedbackLikeRepository;
    private final PostService postService;
    private final CommentFeedbackService commentFeedbackService;

    @Transactional
    public Feedback findOne(Long feedbackId){
        Optional<Feedback> feedback = feedbackRepository.findOne(feedbackId);
        return feedback.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback not found"));
    }

    public int feedbackLikeCount(Long feedbackId){
        Feedback feedback = findOne(feedbackId);
        return feedback.getFeedbackLikeCount();
    }

    public List<Feedback> findAllFeedbacksOfPost(Post post) {
        return feedbackRepository.findByPost(post);
    }

    public List<Feedback> getParentFeedbacksReply(Feedback parentFeedback) {
        return feedbackRepository.findRepliesOfParentFeedback(parentFeedback);
    }

    @Transactional
    public FeedbackLike getFeedbackLikeByMemberIdAndFeedbackId(Long memberId, Long feedbackId) {
        FeedbackLike feedbackLike = feedbackLikeRepository.findByMemberMemberIdAndFeedbackFeedbackId(memberId, feedbackId);
        return feedbackLike;
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

        LocalDateTime time = LocalDateTime.now();

        feedbackObj.setPost(post);
        feedbackObj.setMember(member);
        feedbackObj.setParentFeedback(parentFeedback); //대댓글인 경우 원 댓글의 Feedback, 댓글인 경우 null
        feedbackObj.setPageNumber(pageNumber); //대댓글인 경우 null. parentFeedback.getPageNumber()통헤서 값 넣어도 됩니다.
        feedbackObj.setFeedback(feedback);
        feedbackObj.setFeedbackLikeCount(0);
        feedbackObj.setFeedbackTime(time);
        feedbackRepository.saveFeedback(feedbackObj);

        if (parentFeedback == null) {
            commentFeedbackService.saveCommentFeedback(null, feedbackObj, FEEDBACK, member, post, time);
        }

    }

    @Transactional
    public void modifyFeedback(String feedback, Long feedbackId){
        Feedback feedbackObj = findOne(feedbackId);
        feedbackObj.setFeedback(feedback);
        feedbackRepository.updateFeedback(feedbackObj);
    }

    @Transactional
    public void deleteFeedback(Long feedbackId){
        Feedback feedbackObj = findOne(feedbackId);
        feedbackRepository.deleteFeedback(feedbackObj);
    }

    @Transactional
    public void likeFeedback(Long memberId, Member myMember, Long feedbackId){
        Feedback feedbackObj = findOne(feedbackId);
        Integer feedbackLikeCount = feedbackObj.getFeedbackLikeCount();

        //FeedbackLike를 db에서 조회해보고 조회 결과가 null이면 like+=1, FeedbackLike 엔티티 추가
        // null이 아니면 like -=1, 조회결과인 해당 FeedbackLike 엔티티 삭제
        FeedbackLike feedbackLike = getFeedbackLikeByMemberIdAndFeedbackId(memberId, feedbackId);
        if(feedbackLike == null){
            feedbackObj.setFeedbackLikeCount(feedbackLikeCount + 1); // 좋아요 수 1 증가
            FeedbackLike feedbackLikeObj = FeedbackLike.createFeedbackLike(myMember, feedbackObj);
            feedbackLikeRepository.save(feedbackLikeObj);
        } else{
            feedbackObj.setFeedbackLikeCount(feedbackLikeCount - 1); // 좋아요 수 1 차감
            feedbackLikeRepository.deleteById(feedbackLike.getFeedbackLikeId()); // db에서 삭제
        }
    }
}