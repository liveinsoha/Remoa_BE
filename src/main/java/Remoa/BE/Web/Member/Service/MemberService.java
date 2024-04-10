package Remoa.BE.Web.Member.Service;

import Remoa.BE.Web.Member.Domain.Member;
import Remoa.BE.Web.Member.Dto.GerneralLoginDto.*;
import Remoa.BE.Web.Member.Repository.MemberRepository;
import Remoa.BE.config.jwt.JwtTokenProvider;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final Random random = new Random();

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Long join(Member member) {
        //  validateDuplicateMember(member);
//        member.hashPassword(this.bCryptPasswordEncoder);
        memberRepository.save(member);
        return member.getMemberId();

    }

    public GeneralLoginRes generalLogin(GeneralLoginReq loginReq) {
        Member member = memberRepository.findByEmail(loginReq.getEmail()).orElseThrow(() -> new BaseException(CustomMessage.NO_ID));
        if (!bCryptPasswordEncoder.matches(loginReq.getPassword(), member.getPassword())) {
            throw new BaseException(CustomMessage.UNAUTHORIZED);
        }
        String token = jwtTokenProvider.createToken(member.getEmail());

        return new GeneralLoginRes(token, member);
    }

    private void validateDuplicateMember(Member member) {
        log.info("member={}", member.getEmail());
        Optional<Member> findMembers = memberRepository.findByEmail(member.getEmail());
        if (findMembers.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public void adminSignUp(AdminSignUpReq adminSignUpReq) {
        adminSignUpReq.setPassword(bCryptPasswordEncoder.encode(adminSignUpReq.getPassword()));
        if (memberRepository.existsByEmail(adminSignUpReq.getEmail())) {
            throw new BaseException(CustomMessage.BAD_DUPLICATE);
        }
        memberRepository.save(adminSignUpReq.toEntity());
    }

    public GeneralSignUpRes generalSignUp(GeneralSignUpReq signUpReq) {
        signUpReq.setPassword(bCryptPasswordEncoder.encode(signUpReq.getPassword()));
        if (memberRepository.existsByEmail(signUpReq.getEmail())) {
            throw new BaseException(CustomMessage.BAD_DUPLICATE);
        }

        String uniqueNickname = generateUniqueNickname();

        Member member = signUpReq.toEntity();
        member.setNickname(uniqueNickname);
        Member savedMember = memberRepository.save(member);

        return new GeneralSignUpRes(savedMember.getMemberId());
    }

    private String generateUniqueNickname() {
        String randomNumber;
        boolean nicknameDuplicate;
        do {
            randomNumber = Integer.toString((random.nextInt(900_000) + 100_000));
            nicknameDuplicate = memberRepository.existsByNickname("유저-" + randomNumber);
        } while (nicknameDuplicate);
        return "유저-" + randomNumber;
    }

    public Boolean isNicknameDuplicate(String nickname) {
        List<Member> findMembers = memberRepository.mfindByNickname(nickname);
        return !(findMembers.size() == 0);
    }


    public Member findOne(Long memberId) {
        Optional<Member> member = memberRepository.findOne(memberId);
        return member.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
    }

    public Optional<Member> findByKakaoId(Long kakaoId) {
        return memberRepository.findByKakaoId(kakaoId);
    }


    public Boolean isAdminExist() {
        return memberRepository.findByEmail("spparta@gmail.com").isPresent();
    }
}
