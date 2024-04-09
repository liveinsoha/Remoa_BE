package Remoa.BE.Web.Member.Repository;

import Remoa.BE.Web.Member.Domain.Follow;
import Remoa.BE.Web.Member.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f where f.fromMember = :fromMember and f.toMember = :toMember")
    List<Follow> findFollows(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);

    @Query("select f.toMember from Follow f join f.toMember where f.fromMember = :member order by f.toMember.nickname")
    List<Member> loadFollows(@Param("member") Member member);

    @Query("select f.fromMember from Follow f join f.fromMember where f.toMember = :member order by f.fromMember.nickname")
    List<Member> loadFollowers(@Param("member") Member member);

    @Query("select f.toMember.memberId from Follow f where f.fromMember = :member")
    List<Long> loadFollowsId(@Param("member") Member member);

    @Query("select count(f) > 0 from Follow f where f.fromMember = :fromMember and f.toMember = :toMember")
    boolean isFollow(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);

    @Modifying
    @Query("delete from Follow f where f.fromMember = :fromMember and f.toMember = :toMember")
    void unfollowByMembers(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);

    @Modifying
    @Query("delete from Follow f where f.fromMember = :fromMember and f.toMember = :toMember")
    void deleteByMembers(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);

    // Follow 엔티티와의 관계에 따른 삭제 메소드 추가
    @Modifying
    @Query("delete from Follow f where f.fromMember = :member or f.toMember = :member")
    void deleteFollowsByMember(@Param("member") Member member);
}
