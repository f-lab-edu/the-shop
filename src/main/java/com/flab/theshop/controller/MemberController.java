package com.flab.theshop.controller;

import com.flab.theshop.controller.request.LoginRequest;
import com.flab.theshop.controller.request.SignupRequest;
import com.flab.theshop.controller.response.LoginResponse;
import com.flab.theshop.controller.response.Response;
import com.flab.theshop.controller.response.SignupResponse;
import com.flab.theshop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.flab.theshop.exception.SuccessMessage.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public Response<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        Long memberId = memberService.join(request);
        return Response.success(SIGNUP_OK.getMessage(), new SignupResponse(memberId));
    }

    @GetMapping("/check-userId")
    public Response<Void> checkUserIdDuplicate(@RequestParam("userId") String userId) {
        memberService.validateDuplicateMember(userId);
        return Response.success(AVAILABLE_USERID.getMessage());
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = memberService.login(request.getUserId(), request.getPassword());
        return Response.success(SIGNIN_OK.getMessage(), new LoginResponse(token));
    }
}
