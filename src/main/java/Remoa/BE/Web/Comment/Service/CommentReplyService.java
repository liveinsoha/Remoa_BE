package Remoa.BE.Web.Comment.Service;

import Remoa.BE.Web.Comment.Domain.CommentReply;
import Remoa.BE.Web.Comment.Domain.CommentReplyLike;
import Remoa.BE.Web.Comment.Repository.CommentReplyLikeRepository;
import Remoa.BE.Web.Comment.Repository.CommentReplyRepository;
import Remoa.BE.Web.Comment.Repository.CommentRepository;
import Remoa.BE.Web.Comment.Domain.Comment;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Post;
import Remoa.BE.Web.Post.Repository.PostRepository;
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
public class CommentReplyService {

    private final CommentReplyRepository commentReplyRepository;
    private final CommentReplyLikeRepository commentReplyLikeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<CommentReply> findCommentReplies(Comment comment) {
        List<CommentReply> commentReplies = commentReplyRepository.findByComment(comment);
        return commentReplies;
    }

    public CommentReply registerCommentReply(Member member, String content, Long postId, Long commentId) {
        Post post = postRepository.getReferenceById(postId);
        Comment comment = commentRepository.getReferenceById(commentId);
        CommentReply commentReply = CommentReply.createCommentReply(post, member, content, comment);
        commentReplyRepository.save(commentReply);
        return commentReply;
    }

    public CommentReply findOne(Long replyId) {
        Optional<CommentReply> reply = commentReplyRepository.findById(replyId);
        return reply.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment reply not found"));
    }

    @Transactional
    public void modifyCommentReply(String commentReplyContent, Long commentReplyId) {
        CommentReply commentReply = commentReplyRepository.findById(commentReplyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment reply not found"));
        commentReply.setContent(commentReplyContent); // 변경 감지
    }

    @Transactional
    public void deleteCommentReply(Long commentReplyId) {
        CommentReply commentReply = commentReplyRepository.findById(commentReplyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment reply not found"));
        commentReply.setDeleted(true); // 변경 감지
    }

    public Optional<CommentReplyLike> findCommentReplyLike(Member member, CommentReply commentReply) {
        return commentReplyLikeRepository.findByMemberAndCommentReply(member, commentReply);
    }
}
