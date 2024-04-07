package Remoa.BE.config.auth;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService  implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByNUsername(username).orElseThrow(() -> new UsernameNotFoundException("user name not found!"));
        return new MemberDetails(member);
    }
}
