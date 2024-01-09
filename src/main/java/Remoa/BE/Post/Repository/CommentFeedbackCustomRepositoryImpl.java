package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.*;
import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.QPost;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentFeedbackCustomRepositoryImpl implements CommentFeedbackCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QCommentFeedback commentFeedback = QCommentFeedback.commentFeedback;
    QMember member = QMember.member;
    QComment comment = QComment.comment1;
    QFeedback feedback = QFeedback.feedback1;
    QPost post = QPost.post;

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

    @Override
    public void deleteByMember(Member member) {
        jpaQueryFactory.delete(commentFeedback)
                .where(this.member.eq(member))
                .execute();
    }

    public Page<CommentFeedback> findRecentReceivedCommentFeedback(Member member, Pageable pageable, Category category) {
        boolean isCategoryExists = category != null;
        List<CommentFeedback> resultCommentFeedbacks;
        if(isCategoryExists) {
            resultCommentFeedbacks = jpaQueryFactory.select(commentFeedback)
                    .from(commentFeedback)
                    .join(commentFeedback.post, this.post)
                    .where(
                            this.post.member.eq(member),
                            this.post.category.eq(category)
                    )
                    .orderBy(commentFeedback.time.desc())
                    .offset(pageable.getOffset())   // 페이지 번호
                    .limit(pageable.getPageSize())  // 페이지 사이즈
                    .fetch();
        } else { // 카테고리 구분없이 전체 조회
            resultCommentFeedbacks = jpaQueryFactory.select(commentFeedback)
                    .from(commentFeedback)
                    .join(commentFeedback.post, this.post)
                    .where(this.post.member.eq(member))
                    .orderBy(commentFeedback.time.desc())
                    .offset(pageable.getOffset())   // 페이지 번호
                    .limit(pageable.getPageSize())  // 페이지 사이즈
                    .fetch();
        }
        // 페이지네이션 기능을 위한 쿼리
        JPAQuery<CommentFeedback> countQuery = jpaQueryFactory
                .selectFrom(commentFeedback)
                .join(commentFeedback.post, this.post)
                .where(this.post.member.eq(member));
        // count 쿼리가 필요없는 경우는 실행하지 않는다
        return PageableExecutionUtils.getPage(resultCommentFeedbacks, pageable, () -> countQuery.fetch().size());
    }

}
