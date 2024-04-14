package Remoa.BE.Web.Member.Service;

import Remoa.BE.Web.Member.Domain.AccessToken;
import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Repository.AccessTokenRepository;
import Remoa.BE.Web.Member.Repository.MemberRepository;
import Remoa.BE.config.jwt.JwtTokenProvider;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final AccessTokenRepository accessTokenRepository;


    public void logout(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);

        String account = getAccountFromAccessToken(accessToken);
        Member member = findMemberByAccount(account);

        // AccessToken을 블랙리스트에 추가
        AccessToken blacklistedToken = AccessToken.builder()
                .token(accessToken)
                .member(member)
                .expirationDate(jwtTokenProvider.getAccessTokenExpirationDate(accessToken))
                .blacklisted(true)
                .build();
        accessTokenRepository.save(blacklistedToken);
    }

    private String getAccountFromAccessToken(String accessToken) {
        return jwtTokenProvider.getUserAccount(accessToken);
    }

    private Member findMemberByAccount(String account) {
        return memberRepository.findByAccount(account)
                .orElseThrow(() -> new BaseException(CustomMessage.NO_ID));
    }
}