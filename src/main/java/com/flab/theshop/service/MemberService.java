package com.flab.theshop.service;

import com.flab.theshop.domain.Member;
import com.flab.theshop.dto.member.SignupRequest;
import com.flab.theshop.exception.member.MemberException;
import com.flab.theshop.respository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(SignupRequest request) {
        Member member = Member.builder()
                .name(request.getName())
                .userId(request.getUserId())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        existsByUserId(member.getUserId());
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    /**
     * 중복 회원 있을 시 예외 발생
     */
    private void existsByUserId(String userId) {
        if (memberRepository.existsByUserId(userId)) {
            throw MemberException.DUPLICATE_USERID.get();
        }
    }

    /**
     * 중복 회원 검증
     */
    public void validateDuplicateMember(String userId) {
        existsByUserId(userId);
    }

    /**
     * 로그인
     */
    public String login(String userId, String password) {
        //로그인한 사용자 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);

        //비밀번호 일치하는지 검증
        if (!passwordEncoder.matches(password, member.getPasswordHash())) {
            throw MemberException.INVALID_PASSWORD.get();
        }

        // TODO: 임시 uuid 생성해서 반환(토큰)
        return UUID.randomUUID().toString();
    }
}
