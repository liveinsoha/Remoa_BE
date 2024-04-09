package Remoa.BE.config.jwt;

import Remoa.BE.config.auth.MemberDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private Key key;
    private Long tokenValidTime; //millisecond
    private final MemberDetailsService memberDetailsService;


    public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") String secretKey,
                            @Value("${security.jwt.token.expire-length}") String tokenValidTime,
                            MemberDetailsService memberDetailsService) {

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        ;
        this.tokenValidTime = Long.parseLong(tokenValidTime);
        this.memberDetailsService = memberDetailsService;
    }

    public String createToken(String email) { //email 받음
        Claims claims = Jwts.claims().setSubject(email); // JWT payload에 저장되는 정보 단위
        claims.put("email", email); // key/ value 쌍으로 저장

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidTime); // set Expire Time
//        log.info("now: {}", now);
//        log.info("validity: {}", validity);

        return Jwts.builder()
                .setClaims(claims)  // sub 설정 (정보 저장)
                .setIssuedAt(now)   // 토큰 발행 시간 정보
                .setExpiration(validity) // Set Expire Time
                .signWith(key, SignatureAlgorithm.HS256) //서명하는 값은 우리가 임의로 설정 -> YAML파일
                // 사용할 암호화 알고리즘과 signature에 들어갈 secret값 세팅
                .compact();
    }

    public Long getSubject(String token) {
        return Long.valueOf(Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    // Jwt Token에서 email 추출
    public String getUserAccount(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /*
    인증 성공시 SecurityContextHolder에 저장할 Authentication 객체 생성
    jwt토큰으로부터 이걸 디코딩 할 경우 account를 얻을 수 있다. account를 얻고
    MemberDetailsService에서 account를 통해 MemberDetails객체를 생성.
    */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberDetailsService.loadUserByUsername(this.getUserAccount(token));
        log.info("Jwt 토큰으로부터 account 얻어 냄"); // 토큰으로부터 MemberDetails를 생성하고 이를 토대로 인증 객체를 리턴
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); //헤더 없을 경우 null
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        } //토큰만 파싱해서 리턴
        return null;
    }

    // Token의 유효성 + 만료 기간 검사
    public void validateToken(String jwtToken){
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(jwtToken);
    }

}
