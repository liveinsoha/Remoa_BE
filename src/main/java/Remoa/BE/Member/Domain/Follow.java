package Remoa.BE.Member.Domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Where(clause = "deleted = false")
public class Follow {

    @Id
    @GeneratedValue
    @Column(name = "follow_id")
    private Long followId;

    /**
     * Follow 동작시 Follow를 하는 Member
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    private Member fromMember;

    /**
     * Follow 동작시 Follow를 당하는 Member
     */
    @Column(name = "to_member_id")
    private Long toMemberId;

    /**
     * Follow를 신청한 Member의 follows 필드에 Follow 대상 Member를 세팅
     * @param member
     */
    public void setMember(Member member) {
        this.fromMember = member;
        member.getFollows().add(this);
    }

    /**
     * Follow를 신청하는 멤버와 Follow를 당하는 멤버를 받아와 팔로우 관계를 생성.
     * @param toMember
     * @param fromMember
     * @return Follow
     */
    public static Follow followSomeone(Member toMember, Member fromMember) {
        Follow follow = new Follow();
        follow.setFromMember(fromMember);
        follow.setToMemberId(toMember.getMemberId());

        return follow;
    }

    private Boolean deleted = Boolean.FALSE;

}
