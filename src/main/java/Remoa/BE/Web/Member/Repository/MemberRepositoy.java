package Remoa.BE.Web.Member.Repository;

import Remoa.BE.Web.Member.Domain.Follow;
import Remoa.BE.Web.Member.Domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoy {
    public final EntityManager em;

    public Optional<Member> findOne(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public List<Member> findAll() {
        return this.em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultStream().findAny();
    }

    public List<Member> mfindByNickname(String nickname) {
        return em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getResultList();
    }

    public Optional<Member> findByKakaoId(Long kakaoId) {
        return em.createQuery("select m from Member m where m.kakaoId = :kakaoId", Member.class)
                .setParameter("kakaoId", kakaoId)
                .getResultStream()
                .findAny();
    }

    public void follow(Follow follow) {
        em.persist(follow);
    }

    /**
     * 팔로우 여부를 두 멤버 객체를 받아와서 db에 검색 후 List로 받아서 List에 값이 있으면 팔로우가 되어있는 상태, 없으면 팔로우 되어있지 않은 상태.
     * 팔로우/언팔로우 기능 전에 이 메서드를 통해 이후 동작을 정할 수 있다.
     * @param fromMember
     * @param toMember
     * @return 팔로우 되어있지 않음 : false, 팔로우 되어있음 : true
     */
    public Boolean isFollow(Member fromMember, Member toMember) {
        return !em.createQuery("select f from Follow f " +
                        "where f.fromMember = :fromMember and f.toMember = :toMember", Follow.class)
                .setParameter("fromMember", fromMember)
                .setParameter("toMember", toMember)
                .getResultList()
                .isEmpty();
    }

    /**
     * 두 멤버가 팔로우가 되어있으면 그에 맞는 Follow를 불러와줌.
     * @param fromMember
     * @param toMember
     * @return
     */
    public Follow loadFollow(Member fromMember, Member toMember) {
        return em.createQuery("select f from Follow f " +
                                "where f.fromMember = :fromMember and f.toMember = :toMember", Follow.class)
                .setParameter("fromMember", fromMember)
                .setParameter("toMember", toMember)
                .getResultList()
                .get(0);
    }

    /**
     * 멤버가 팔로우하는 모든 멤버를 불러와줌.
     * @param member
     * @return List<Member>
     */
    public List<Member> loadFollows(Member member) {
        return em.createQuery("select f.toMember from Follow f " +
                                "where f.fromMember = :member " +
                                "order by f.toMember.nickname", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    /**
     * 팔로워 Member 객체 리스트 반환
     * @param member
     * @return List<Member>
     */
    public List<Member> loadFollowers(Member member){
        return em.createQuery("select f.fromMember from Follow f " +
                                "where f.toMember = :member " +
                                "order by f.fromMember.nickname", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    /**
     * 멤버가 팔로우하는 모든멤버의 아이디를 불러와줌
     * @param member
     * @return
     */
    public List<Long> loadFollowsId(Member member) {
        return em.createQuery("select f.toMember.memberId from Follow f " +
                        "where f.fromMember = :member", Long.class)
                .setParameter("member", member)
                .getResultList();
    }

    /**
     * Follow/Unfollow는 빈번하게 일어나므로 Soft Delete를 쓰기보단 Hard Delete를 쓰는 게 나을 것 같아 Hard Delete를 적용.
     * @param
     */
    public void unfollowByFollowId(Member fromMember, Member toMember) {
        Follow result = em.createQuery("select f from Follow f " +
                        "where f.fromMember = :fromMember and f.toMember = :toMember", Follow.class)
                .setParameter("fromMember", fromMember)
                .setParameter("toMember", toMember).getSingleResult();
        em.remove(result);
    }

    /**
     * 회원 탈퇴시 팔로우 관계를 모두 삭제.
     * @param member
     */
    public void deleteAllFollowshipByMember(Member member){
        List<Follow> results = em.createQuery("select f from Follow f " +
                "where (f.fromMember = :member or f.toMember = :member)", Follow.class)
                .setParameter("member", member)
                .getResultList();
        results.stream().forEach(res -> {
            em.remove(res);
        });
    }


    //soft delete 메소드로 사용하려 하였으나, 영속성 컨텍스트를 통한 엔티티의 deleted 필드값 교체만으로도 동작이 가능해서 현재 잠정 폐기
    @Deprecated
    @Query("update Member m set m.deleted = true where m.memberId = :id")
    @Modifying
    public void deleteSoftlyById(Member member) {
        log.info("delete member...");
        em.createQuery("update Member m set m.deleted = true where m.memberId = :id")
                .setParameter("id", member.getMemberId());
    }
}
