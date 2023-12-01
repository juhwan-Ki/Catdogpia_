package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.admin.ConsultationDto;
import com.likelion.catdogpia.domain.dto.admin.ConsultationListDto;
import com.likelion.catdogpia.domain.entity.consultation.Consultation;
import com.likelion.catdogpia.domain.entity.consultation.ConsultationAnswer;
import com.likelion.catdogpia.domain.entity.user.Member;
import com.likelion.catdogpia.repository.ConsultationAnswerRepository;
import com.likelion.catdogpia.repository.ConsultationRepository;
import com.likelion.catdogpia.repository.MemberRepository;
import com.likelion.catdogpia.repository.QueryRepository;
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
public class ConsultationManagementService {

    // 1:1 문의 관련
    private final ConsultationRepository consultationRepository;
    private final ConsultationAnswerRepository consultationAnswerRepository;
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

    // 1:1문의 목록 조회
    public Page<ConsultationListDto> findConsultationList(Pageable pageable, String filter, String keyword, String toDate, String fromDate) {
        return queryRepository.findByConsultationList(pageable, filter, keyword, toDate, fromDate);
    }

    // 1:1 문의 삭제
    @Transactional
    public void deleteConsultationList(List<Long> deleteList, String loginId) {
        isAdmin(loginId);
        // id가 있으면 삭제
        for (Long deleteId : deleteList) {
            if(consultationRepository.existsById(deleteId)) {
                consultationRepository.deleteById(deleteId);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    // 1:1 문의 상세 조회
    public ConsultationDto findConsultation(Long consulId) {
        Consultation findConsultation = consultationRepository.findById(consulId).orElseThrow(IllegalArgumentException::new);
        return ConsultationDto.fromEntity(findConsultation);
    }

    // 1:1문의 답변 등록 / 업데이트
    @Transactional
    public void modifyConsultationAnswer(Long consulId, String answer, String loginId) {
        Consultation findConsultation = consultationRepository.findById(consulId).orElseThrow(IllegalArgumentException::new);

        // 사용자 권한 가져오도록 변경 필요
        Member answerer = memberRepository.findByLoginId(loginId).orElseThrow(IllegalArgumentException::new);
        // 관리자인지 확인
        if(!answerer.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
        // 답글이 존재하지 않으면 새로 등록
        if(findConsultation.getConsultationAnswer() == null) {
            consultationAnswerRepository.save(ConsultationAnswer.builder()
                    .answer(answer)
                    .consultation(findConsultation)
                    .member(answerer)
                    .build());
        } else {
            // 답글이 이미 등록되어 있지만 답변을 변경하고 싶으면 답변을 수정하도록 함
            findConsultation.getConsultationAnswer().changeAnswer(answer, answerer);
        }
    }
}
