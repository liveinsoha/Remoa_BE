package Remoa.BE.Web.Member.Service;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Dto.Req.KakaoLoginRequestDto;
import Remoa.BE.Web.Member.Dto.Res.KakaoLoginResponseDto;
import Remoa.BE.Web.Member.Dto.kakaoLoginDto.KakaoProfile;
import Remoa.BE.Web.Member.Dto.kakaoLoginDto.OAuthToken;
import Remoa.BE.Web.Member.Repository.MemberRepository;
import Remoa.BE.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {

    private final Random random = new Random();

    //카카오 로그인시 접속해야 할 링크 : https://kauth.kakao.com/oauth/authorize?client_id=139febf9e13da4d124d1c1faafcf3f86&redirect_uri=http://localhost:8080/login/kakao&response_type=code

    private static String password = "password";
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoLoginResponseDto kakaoLogin(String code) {

        OAuthToken accessToken = getAccessToken(code);
        KakaoProfile kakaoProfile = getKakaoProfile(accessToken);

        log.info("kakaoProfile = {}", kakaoProfile);

        String uniqueNickname = generateUniqueNickname();

        KakaoLoginRequestDto kakaoLoginRequestDto = new KakaoLoginRequestDto(kakaoProfile, uniqueNickname);
        Member member = memberRepository.findByKakaoId(kakaoLoginRequestDto.getKakaoIdentifier()).orElseGet(() -> null);
        if (member == null) {
            log.info("카카오로 회원가입");
            member = memberRepository.save(kakaoLoginRequestDto.toEntity());
        }
        String token = jwtTokenProvider.createToken(member.getEmail()); //임의로 만든 account로 토큰 생성.
        return new KakaoLoginResponseDto(token, member.getName(), member.getMemberId(), member.getNickname());
    } // 그냥 회원 가입 할 경우는 로그인을 따로 진행해야 토큰을 주고, 카카오 로그인을 할 경우 처음 등록시에도 토큰을 부여? -> yes

    private String generateUniqueNickname() {
        String randomNumber;
        boolean nicknameDuplicate;
        do {
            randomNumber = Integer.toString((random.nextInt(900_000) + 100_000));
            nicknameDuplicate = memberRepository.existsByNickname("유저-" + randomNumber);
        } while (nicknameDuplicate);
        return "유저-" + randomNumber;
    }

    //(2)
    // 발급 받은 accessToken 으로 카카오 회원 정보 얻기
    public KakaoProfile getKakaoProfile(OAuthToken oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = new HttpEntity<>(httpHeaders); //헤더만 가지고 요청헤더를 만들 수 있다.
        KakaoProfile kakaoProfile = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoProfileRequest2, KakaoProfile.class).getBody();

        return kakaoProfile;
    }

    // (1)넘어온 인가 코드를 통해 access_token 발급
    public OAuthToken getAccessToken(String code) {
        //POST 방식으로 key=value 데이터를 요청
        //Post 요청을 하는 다양한 라이브러리가 있다 Retrofit(안드로이드), OkHttp, RestTempate
        RestTemplate restTemplate = new RestTemplate();


        //Haeder 오브젝트 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        log.info("before");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "adf170bad1bb693217a3ee4dc49ccf0c");
        params.add("redirect_uri", "http://localhost:3000/login/callback");
        params.add("code", code);
        //params.add("client_secret", client_secret);

        log.info("after");
        /**
         * 토큰을 발급할때 좀 더 보안을 강화하기 위해 Client Secret을 사용할 수 있다.
         * Client Secret을 받는 위치는 내 애플리케이션 -> 제품 설정 -> 카카오로그인 -> 보안 입니다.
         * Client Secret을 받고난후 밑에 활성화 상태를 사용함으로 변경해주어야한다.
         */

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담는다
        //HttpHeader와 HttpBody 담기기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, httpHeaders); //

        //Http요청하기 그리고 response 변수의 응답 받음.
        OAuthToken oAuthToken = restTemplate.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest, OAuthToken.class).getBody();

        return oAuthToken;
    }

}