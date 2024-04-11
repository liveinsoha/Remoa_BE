package Remoa.BE.Web.Post.Repository;

import Remoa.BE.Web.Member.Domain.Member;

import Remoa.BE.Web.Member.Domain.QMember;
import Remoa.BE.Web.Post.Domain.Post;
import Remoa.BE.Web.Post.Domain.PostScrap;
import Remoa.BE.Web.Post.Domain.QPost;
import Remoa.BE.Web.Post.Domain.QPostScrap;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    QMember member = QMember.member;
    QPost post = QPost.post;
    QPostScrap postScrap = QPostScrap.postScrap;
    
    @Override
    public List<Post> findByMemberRecentTwelve(Member member) {
        return jpaQueryFactory.select(postScrap)
                .from(postScrap)
                .join(postScrap.member, this.member)
                .where(this.member.eq(member))
                .orderBy(postScrap.scrapTime.desc())
                .limit(12L)
                .fetch()
                .stream().map(PostScrap::getPost)
                .collect(Collectors.toList());
        
    }
}
