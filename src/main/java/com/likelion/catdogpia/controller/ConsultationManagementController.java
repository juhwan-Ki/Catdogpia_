package com.likelion.catdogpia.controller;

import com.likelion.catdogpia.domain.entity.consultation.ConsulClassification;
import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.ConsultationManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/consultations")
public class ConsultationManagementController {

    private final ConsultationManagementService consultationService;
    private final JwtTokenProvider jwtTokenProvider;

    // 1:1문의 목록
    @GetMapping()
    public String consultationsList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String fromDate
    ) {
        model.addAttribute("consultationList", consultationService.findConsultationList(pageable, filter, keyword, toDate, fromDate));
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("toDate", toDate);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("classificationList", Arrays.asList(ConsulClassification.values()));
        return "page/admin/consultations";
    }

    // 1:1 문의 삭제
    @PostMapping("/delete-list")
    @ResponseBody
    public ResponseEntity<String> consultationsDelete(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody List<Map<String, Object>> requestList
    ) {
        // 토큰이 없으면 오류 발생
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        // 한번더 체크
        if(requestList.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            List<Long> deleteList = new ArrayList<>();
            // deleteList 생성
            for (Map<String, Object> map : requestList) {
                deleteList.add(Long.valueOf((String) map.get("id")));
            }
            consultationService.deleteConsultationList(deleteList, loginId);
        }

        return ResponseEntity.ok(HttpStatus.OK.name());
    }

    // 1:1 문의 상세
    @GetMapping("/{consulId}")
    public String consultationDetails(@PathVariable Long consulId, Model model) {
        model.addAttribute("classificationList", Arrays.asList(ConsulClassification.values()));
        model.addAttribute("consultation", consultationService.findConsultation(consulId));
        return "page/admin/consultation-detail";
    }

    // 1:1 문의 답변 등록 / 업데이트
    @PostMapping("/{consulId}/update-answer")
    @ResponseBody
    public ResponseEntity<String> consultationUpdateAnswer(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long consulId,
            @RequestBody Map<String, String> requestBody
    ){
        // 토큰이 없으면 오류 발생
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        //
        String answer = requestBody.get("answer");
        consultationService.modifyConsultationAnswer(consulId, answer, loginId);
        return ResponseEntity.ok(HttpStatus.OK.name());
    }
}
