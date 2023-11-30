package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.admin.ReportDto;
import com.likelion.catdogpia.domain.dto.admin.ReportListDto;
import com.likelion.catdogpia.domain.entity.report.Report;
import com.likelion.catdogpia.domain.entity.user.Member;
import com.likelion.catdogpia.repository.MemberRepository;
import com.likelion.catdogpia.repository.QueryRepository;
import com.likelion.catdogpia.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportManagementService {

    // 신고 관련
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    // 공통
    private final QueryRepository queryRepository;

    // 관리자인지 확인하는 메소드
    private void isAdmin(String loginId) {
        Member requestMember = memberRepository.findByLoginId(loginId).orElseThrow(IllegalArgumentException::new);
        // 관리자인지 확인
        if(!requestMember.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
    }

    // 신고관리 목록
    public Page<ReportListDto> findReportList(Pageable pageable, String filter, String keyword, String toDate, String fromDate) {
        return queryRepository.findByReportList(pageable, filter, keyword, toDate, fromDate);
    }

    // 신고 삭제
    @Transactional
    public void deleteReportList(List<Long> deleteList, String loginId) {
        isAdmin(loginId);
        // id가 있으면 삭제
        for (Long deleteId : deleteList) {
            if(reportRepository.existsById(deleteId)) {
                reportRepository.deleteById(deleteId);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    // 신고 상세조회
    public ReportDto findReport(Long reportId) {
        Report findReport = reportRepository.findById(reportId).orElseThrow(IllegalArgumentException::new);
        return ReportDto.fromEntity(findReport);
    }

    // 신고 처리
    @Transactional
    public void processedReport(String loginId, Long reportId) {
        Report findReport = reportRepository.findById(reportId).orElseThrow(IllegalArgumentException::new);
        // 관리자 정보 가져오기
        Member manager = memberRepository.findByLoginId(loginId).orElseThrow(IllegalArgumentException::new);
        // 관리자인지 확인
        if(!manager.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
        // 신고 처리
        findReport.processed();
//        // 신고된 댓글 안보이게
//        if(findReport.getArticle() != null) {
//            communityRepository.delete(findReport.getArticle());
//        } else {
//            // 댓글이면 댓글 삭제
//            commentRepository.delete(findReport.getComment());
//        }
        // 신고 횟수가 3회 이상인 사람은 블랙리스트로 변경
        if(reportRepository.countByWriterAndProcessedAtIsNotNull(findReport.getWriter()) >= 3) {
            findReport.getWriter().changeBlackListYn();
            log.info("블랙리스트 처리");
        }
    }
}
