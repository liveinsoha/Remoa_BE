package Remoa.BE.Member.Domain;

import lombok.Getter;
import lombok.Setter;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    private Member toMember;

    /**
     * Follow를 신청한 Member의 follows 필드에 Follow 대상 Member를 세팅
     * @param member
     */
    public void setMember(Member member) {
        this.fromMember = member;
        member.getFollows().add(this);
    }


    private Boolean deleted = Boolean.FALSE;

}
