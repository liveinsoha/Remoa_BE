package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카카오 로그인시 사용할 필요 없다
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Deprecated
public class LoginService {

    private final MemberRepository MemberRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public Member login(String email, String password) {
        return MemberRepository.findByEmail(email)
                .stream().filter(member -> bCryptPasswordEncoder.matches(password, member.getPassword())).findAny().orElse(null);
    }

}