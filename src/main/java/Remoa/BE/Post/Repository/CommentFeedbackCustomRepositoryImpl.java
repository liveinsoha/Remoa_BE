package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentFeedbackCustomRepositoryImpl implements CommentFeedbackCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QCommentFeedback commentFeedback = QCommentFeedback.commentFeedback;
    QMember member = QMember.member;
    QComment comment = QComment.comment1;
    QFeedback feedback = QFeedback.feedback1;

    @Override
    public Optional<CommentFeedback> findByMemberOrderByTime(Member member) {
        return jpaQueryFactory.select(commentFeedback)
                .from(commentFeedback)
                .join(commentFeedback.member, this.member)
                .where(this.member.eq(member))
                .orderBy(commentFeedback.time.desc())
                .limit(1L).stream().findAny();
    }

    @Override
    public Optional<CommentFeedback> findByComment(Comment comment) {
        return jpaQueryFactory.select(commentFeedback)
                .from(commentFeedback)
                .join(commentFeedback.comment, this.comment)
                .where(this.comment.eq(comment))
                .stream().findAny();
    }

    @Override
    public Optional<CommentFeedback> findByFeedback(Feedback feedback) {
        return jpaQueryFactory.select(commentFeedback)
                .from(commentFeedback)
                .join(commentFeedback.feedback, this.feedback)
                .where(this.feedback.eq(feedback))
                .stream().findAny();
    }

}
