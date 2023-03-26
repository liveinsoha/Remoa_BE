package Remoa.BE.Post.Repository;

import Remoa.BE.Member.Domain.CommentFeedback;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentFeedbackCustomRepositoryImpl implements CommentFeedbackCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommentFeedback> findByMemberOrderByTimeDesc() {
        return null;
    }
}
