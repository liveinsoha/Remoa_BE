package Remoa.BE.Member.Repository;

import Remoa.BE.Member.Domain.Follow;
import Remoa.BE.Member.Domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MemberRepository {
    public final EntityManager em;

    public void save(Member member) {
        this.em.persist(member);
        log.info("member save ok = {}", member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return this.em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultStream().findAny();
    }

    public List<Member> findByNickname(String nickname) {
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
                        "where f.fromMember = :fromMember and f.toMemberId = :toMemberId", Follow.class)
                .setParameter("fromMember", fromMember)
                .setParameter("toMemberId", toMember.getMemberId())
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
                        "where f.fromMember = :fromMember and f.toMemberId = :toMemberId", Follow.class)
                .setParameter("fromMember", fromMember)
                .setParameter("toMemberId", toMember.getMemberId())
                .getResultList()
                .get(0);
    }

    /**
     * 멤버가 팔로우하는 모든 멤버를 불러와줌.
     * @param member
     * @return List<Member>
     */
    public List<Member> loadFollows(Member member) {
        return em.createQuery("select f from Follow f " +
                        "where f.fromMember = :member", Follow.class)
                .setParameter("member", member)
                .getResultList()
                .stream()
                .map(follow -> {
                    Long toMemberId = follow.getToMemberId();
                    return this.findOne(toMemberId);
                })
                .collect(Collectors.toList());
    }

    /**
     * Follow/Unfollow는 빈번하게 일어나므로 Soft Delete를 쓰기보단 Hard Delete를 쓰는 게 나을 것 같아 Hard Delete를 적용.
     * @param followId
     */
    public void unfollowByFollowId(Long followId) {
        Follow followForDelete = em.find(Follow.class, followId);
        if (followForDelete != null) {
            em.remove(followForDelete);
        } else {
            throw new EntityNotFoundException("Entity with follow id : " + followId + " not found!");
        }
    }

    //soft delete 메소드로 사용하려 하였으나, 영속성 컨텍스트를 통한 엔티티의 deleted 필드값 교체만으로도 동작이 가능해서 현재 잠정 폐기
/*    @Query("update Member m set m.deleted = true where m.memberId = :id")
    @Modifying*/
    /*public void deleteSoftlyById(Member member) {
        log.info("delete member...");
        em.createQuery("update Member m set m.deleted = true where m.memberId = :id")
                .setParameter("id", member.getMemberId());
    }*/
}
