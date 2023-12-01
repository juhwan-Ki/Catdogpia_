package com.likelion.catdogpia.controller;

import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.ReportManagementService;
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
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
public class ReportManagementController {

    private final ReportManagementService reportService;
    private final JwtTokenProvider jwtTokenProvider;

    // 신고 관리 목록
    @GetMapping()
    public String reportList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String fromDate
    ){
        model.addAttribute("reportList", reportService.findReportList(pageable, filter, keyword, toDate, fromDate));
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("toDate", toDate);
        model.addAttribute("fromDate", fromDate);
        return "page/admin/reports";
    }

    // 신고 삭제
    @PostMapping("/delete-list")
    @ResponseBody
    public ResponseEntity<String> reportDelete(
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
            reportService.deleteReportList(deleteList, loginId);
        }

        return ResponseEntity.ok(HttpStatus.OK.name());
    }

    // 신고 상세
    @GetMapping("/{reportId}")
    public String reportDetails(@PathVariable Long reportId, Model model) {
        model.addAttribute("report", reportService.findReport(reportId));
        return "page/admin/report-detail";
    }

    // 신고 처리
    @PostMapping("/{reportId}/processed")
    @ResponseBody
    public ResponseEntity<String> reportProcessed(@RequestHeader("Authorization") String accessToken, @PathVariable Long reportId){
        // 토큰이 없으면 오류 발생
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        //
        reportService.processedReport(loginId, reportId);

        return ResponseEntity.ok(HttpStatus.OK.name());
    }
}
