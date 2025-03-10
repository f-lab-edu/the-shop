package com.flab.theshop.dto.member;

import com.flab.theshop.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "아이디는 필수입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "비밀번호는 숫자, 문자를 포함한 8글자 이상이어야 합니다.")
    private String password;

    private Role role;

    @NotBlank(message = "핸드폰번호는 필수입니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
            message = "휴대폰 번호는 01X-XXX(X)-XXXX 형식이어야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    @Size(max = 100, message = "주소는 100자 이하로 입력해주세요.")
    private String address;

}
