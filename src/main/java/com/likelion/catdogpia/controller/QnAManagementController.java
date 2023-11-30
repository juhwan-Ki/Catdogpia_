package com.likelion.catdogpia.controller;


import com.likelion.catdogpia.domain.dto.admin.QnaListDto;
import com.likelion.catdogpia.domain.entity.product.QnAClassification;
import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.QnAManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/admin/qna")
public class QnAManagementController {

    private final QnAManagementService qnAService;
    private final JwtTokenProvider jwtTokenProvider;

    // QnA목록
    @GetMapping()
    public String qnaList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String fromDate
    ) {
        Page<QnaListDto> qnaList = qnAService.findQnaList(pageable, filter, keyword, toDate, fromDate);
        model.addAttribute("qnaList", qnaList);
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("toDate", toDate);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("classificationList", Arrays.asList(QnAClassification.values()));
        return "page/admin/qna";
    }

    // QnA 삭제
    @PostMapping("/delete-list")
    @ResponseBody
    public ResponseEntity<String> qnaDelete(@RequestHeader("Authorization") String accessToken, @RequestBody List<Map<String, Object>> requestList) {
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
            qnAService.deleteQnaList(deleteList,loginId);
        }

        return ResponseEntity.ok("ok");
    }

    // QnA 상세
    @GetMapping("/{qnaId}")
    public String qnaDetails(@PathVariable Long qnaId, Model model) {
        model.addAttribute("classificationList", Arrays.asList(QnAClassification.values()));
        model.addAttribute("qna", qnAService.findQna(qnaId));
        return "page/admin/qna-detail";
    }

    // QnA 답변 등록 / 업데이트
    @PostMapping("/{qnaId}/update-answer")
    @ResponseBody
    public ResponseEntity<String> qnaUpdateAnswer(@RequestHeader("Authorization") String accessToken, @PathVariable Long qnaId, @RequestBody Map<String, String> requestBody) {
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        String answer = requestBody.get("answer");
        qnAService.modifyQnaAnswer(qnaId, answer, loginId);

        return ResponseEntity.ok("ok");
    }
}
