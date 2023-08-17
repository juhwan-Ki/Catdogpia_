package com.likelion.catdogpia.domain.entity.user;

import com.likelion.catdogpia.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String loginId;

    @Column(length = 20, nullable = false)
    private String password;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 60, nullable = false, unique = true)
    private String email;

    @Column(length = 30, nullable = false, unique = true)
    private String nickname;

    @Column(length = 11, nullable = false, unique = true)
    private String phone;

    @Column(length = 5, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1)
    private Character socialLogin;

    @Column(length = 1, nullable = false)
    private Character blackListYn;

    @Builder
    public Member(Long id, String loginId, String password, String name, String email, String nickname, String phone, Role role, Character socialLogin, Character blackListYn) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.role = role;
        this.socialLogin = socialLogin;
        this.blackListYn = blackListYn;
    }
}