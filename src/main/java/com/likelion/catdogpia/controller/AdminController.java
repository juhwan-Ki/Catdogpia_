package com.likelion.catdogpia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.catdogpia.domain.dto.admin.*;
import com.likelion.catdogpia.domain.dto.notice.NoticeDto;
import com.likelion.catdogpia.domain.entity.consultation.ConsulClassification;
import com.likelion.catdogpia.domain.entity.product.OrderStatus;
import com.likelion.catdogpia.domain.entity.product.QnAClassification;
import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.AdminService;
import com.likelion.catdogpia.service.MemberManagementService;
import com.likelion.catdogpia.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final MemberManagementService memberService;
    private final NoticeService noticeService;
    private final JwtTokenProvider jwtTokenProvider;

    // 관리자 메인 페이지
    @GetMapping("/main")
    public String mainPage(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        model.addAttribute("counts", adminService.findTotalCounts());
        model.addAttribute("memberList", memberService.findMemberList(pageable, null, null));
        return "page/admin/index";
    }

    // 공지사항 목록
    @GetMapping("/notices")
    public String noticeList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword
    ) {
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("noticeList", noticeService.findNoticeList(pageable, filter, keyword));
        return "page/admin/notices";
    }

    // 공지사항 등록 페이지
    @GetMapping("/notices/create-form")
    public String noticeCreateForm() {
        return "/page/admin/notice-create";
    }

    // 공지사항 등록
    @PostMapping("/notices/create")
    public ResponseEntity<String> noticeCreate(@RequestHeader("Authorization") String accessToken, @RequestBody NoticeDto noticeDto) {
        // 토큰이 없으면 오류 발생
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        //
        noticeService.noticeCreate(loginId, noticeDto);

        return ResponseEntity.ok(HttpStatus.OK.name());
    }

    // 공지사항 상세
    @GetMapping("/notices/{noticeId}")
    public String noticeDetails(@PathVariable Long noticeId, Model model) {
        model.addAttribute("notice", noticeService.findNotice(noticeId));
        return "page/admin/notice-detail";
    }

    // 공지사항 수정 페이지
    @GetMapping("/notices/{noticeId}/modify-form")
    public String noticeModifyForm(@PathVariable Long noticeId, Model model) {
        model.addAttribute("notice", noticeService.findNotice(noticeId));
        return "page/admin/notice-modify";
    }

    // 공지사항 수정
    @PostMapping("/notices/{noticeId}/modify")
    public ResponseEntity<String> noticesModify(@PathVariable Long noticeId, @RequestHeader("Authorization") String accessToken, @RequestBody NoticeDto noticeDto) {
        // 토큰이 없으면 오류 발생
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        //
        noticeService.noticeModify(loginId, noticeId, noticeDto);

        return ResponseEntity.ok(HttpStatus.OK.name());
    }

    // 공지사항 삭제
    @PostMapping("/notices/delete-list")
    public ResponseEntity<String> noticeDelete(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody List<Map<String, Object>> requestList
    ) {
        // 토큰이 없으면 오류 발생
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 한번더 체크
        if(requestList.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            List<Long> deleteList = new ArrayList<>();
            // deleteList 생성
            for (Map<String, Object> map : requestList) {
                deleteList.add(Long.valueOf((String) map.get("id")));
            }
            noticeService.deleteNoticeList(deleteList);
        }

        return ResponseEntity.ok(HttpStatus.OK.name());
    }

}