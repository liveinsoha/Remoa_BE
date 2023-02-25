package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WithdrewService {

    /**
     * Dirty Checking 기능을 통한 엔티티의 deleted 필드를 true로 만들어 soft delete 시켜줌.
     * @param member
     */
    @Transactional
    public void withdrewRemoa(Member member) {
        member.setDeleted(true);
    }
}
