package com.flab.theshop.service;

import com.flab.theshop.controller.request.SignupRequest;
import com.flab.theshop.domain.Member;
import com.flab.theshop.exception.ErrorCode;
import com.flab.theshop.exception.MemberTaskException;
import com.flab.theshop.respository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.flab.theshop.exception.ErrorCode.*;

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

        validateDuplicateMember(member.getUserId());
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    /**
     * 중복 회원 검증
     */
    public void validateDuplicateMember(String userId) {
        if (memberRepository.existsByUserId(userId)) {
            throw new MemberTaskException(E409_DUPLICATE_USERID);
        }
    }

}
