package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.CommentFeedback;

import java.util.List;

public interface CommentFeedbackCustomRepository {

    List<CommentFeedback> findByMemberOrderByTimeDesc();
}
