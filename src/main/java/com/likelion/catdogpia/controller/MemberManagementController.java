package com.likelion.catdogpia.controller;

import com.likelion.catdogpia.domain.dto.admin.MemberDto;
import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.MemberManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class MemberManagementController {

    private final MemberManagementService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    // 관리자 회원관리 목록
    @GetMapping()
    public String memberList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword
    ) {
        model.addAttribute("memberList", memberService.findMemberList(pageable, filter, keyword));
        model.addAttribute("filter",filter);
        model.addAttribute("keyword",keyword);
        return "page/admin/members";
    }

    // 관리자 회원 관리 상세
    @GetMapping("/members/{memberId}")
    public String memberDetails(@PathVariable Long memberId, Model model) {

        MemberDto member = memberService.findMember(memberId);
        // 금액 포맷 맞추기
        member.changeFormat(member);
        log.info("member : " + member.toString());
        model.addAttribute("member", member);
        return "page/admin/member-detail";
    }

    // 관리자 회원 수정 페이지
    @GetMapping("/{memberId}/modify-form")
    public String memberModifyForm(@PathVariable Long memberId, Model model) {
        MemberDto member = memberService.findMember(memberId);

        member.changeFormat(member);
        model.addAttribute("member", member);
        return "page/admin/member-update";
    }

    // 사용자 정보 수정
    @PostMapping("/{memberId}/modify")
    @ResponseBody
    public ResponseEntity<String> memberModify(@RequestHeader("Authorization") String accessToken, @RequestBody MemberDto memberDto, @PathVariable Long memberId) {
        log.info("dto.toString() : " + memberDto.toString());
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        //
        memberService.modifyMember(memberDto, memberId, loginId);
        return ResponseEntity.ok("ok");
    }

    // 이메일, 닉네임 중복 확인
    @GetMapping("/check-duplicate")
    @ResponseBody // JSON 응답을 위한 어노테이션 추가
    public Map<String, Boolean> checkEmailAvailability(
            @RequestParam("email") String email,
            @RequestParam("nickname") String nickname,
            @RequestParam("memberId") Long memberId
    ) {
        log.info("email : " + email);
        log.info("nickname : " + nickname);
        log.info("memberId : " + memberId);
        Map<String, Boolean> resultMap = new HashMap<>();
        resultMap.put("duplicated", memberService.isDuplicated(email, nickname, memberId));
        log.info("duplicated : " + resultMap.get("duplicated"));
        return resultMap;
    }

    // 회원 삭제
    @PostMapping("/{memberId}/delete")
    @ResponseBody
    public ResponseEntity<String> memberRemove(@RequestHeader("Authorization") String accessToken, @PathVariable Long memberId) {
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();

        memberService.removeMember(memberId, loginId);
        return ResponseEntity.ok("ok");
    }
}
