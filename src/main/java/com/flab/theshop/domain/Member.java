package com.flab.theshop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String userId; //사용자가 입력한 아이디

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; //[구매자, 판매자]

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Builder
    public Member(String name, String userId, String passwordHash, Role role, String phoneNumber, String address) {
        this.name = name;
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}