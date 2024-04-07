package Remoa.BE.Web.Post.Repository;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Post.Domain.Post;

import java.util.List;

public interface PostCustomRepository {

    List<Post> findByMemberRecentTwelve(Member member);
    
}
