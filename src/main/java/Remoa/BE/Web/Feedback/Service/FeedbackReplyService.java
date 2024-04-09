package Remoa.BE.Web.Feedback.Service;


import Remoa.BE.Web.Feedback.Domain.Feedback;
import Remoa.BE.Web.Feedback.Domain.FeedbackReply;
import Remoa.BE.Web.Feedback.Domain.FeedbackReplyLike;
import Remoa.BE.Web.Feedback.Repository.FeedBackReplyLikeRepository;
import Remoa.BE.Web.Feedback.Repository.FeedBackReplyRepository;
import Remoa.BE.Web.Feedback.Repository.FeedbackRepository;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Post;
import Remoa.BE.Web.Post.Repository.PostRepository;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedbackReplyService {

    private final PostRepository postRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedBackReplyRepository feedbackReplyRepository;
    private final FeedBackReplyLikeRepository feedBackReplyLikeRepository;


    public FeedbackReply registerFeedbackReply(Member member, Long postId, Long feedbackId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(CustomMessage.NO_ID));;
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new BaseException(CustomMessage.NO_ID));;
        FeedbackReply feedbackReply = FeedbackReply.createFeedbackReply(post, member, feedback, content);
        feedbackReplyRepository.save(feedbackReply);
        return feedbackReply;
    }


    public List<FeedbackReply> findFeedbackReplies(Feedback parentFeedback) {
        return feedbackReplyRepository.findByFeedback(parentFeedback);
    }

    public FeedbackReply findOne(Long replyId) {
        return feedbackReplyRepository.findById(replyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback reply not found"));
    }

    @Transactional
    public void modifyFeedbackReply(String feedbackReplyContent, Long feedbackReplyId) {
        FeedbackReply feedbackReply = feedbackReplyRepository.findById(feedbackReplyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback reply not found"));
        feedbackReply.setContent(feedbackReplyContent); // 변경 감지
    }

    @Transactional
    public void deleteFeedbackReply(Long feedbackReplyId) {
        FeedbackReply feedbackReply = feedbackReplyRepository.findById(feedbackReplyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback reply not found"));
        feedbackReply.setDeleted(true); // 변경 감지
    }

    public Optional<FeedbackReplyLike> findFeedbackReplyLike(Member member, FeedbackReply feedbackReply) {
        return feedBackReplyLikeRepository.findByMemberAndFeedbackReply(member, feedbackReply);
    }
}
