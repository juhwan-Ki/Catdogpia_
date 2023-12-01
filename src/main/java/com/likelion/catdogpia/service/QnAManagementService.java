package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.admin.QnADto;
import com.likelion.catdogpia.domain.dto.admin.QnaListDto;
import com.likelion.catdogpia.domain.entity.product.QnA;
import com.likelion.catdogpia.domain.entity.product.QnAAnswer;
import com.likelion.catdogpia.domain.entity.user.Member;
import com.likelion.catdogpia.repository.MemberRepository;
import com.likelion.catdogpia.repository.QnAAnswerRepository;
import com.likelion.catdogpia.repository.QnaRepository;
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
public class QnAManagementService {

    // 회원관련
    private final MemberRepository memberRepository;
    // QnA 관련
    private final QnaRepository qnaRepository;
    private final QnAAnswerRepository qnAAnswerRepository;
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

    // QnA목록 조회
    public Page<QnaListDto> findQnaList(Pageable pageable, String filter, String keyword, String toDate, String fromDate) {
        return queryRepository.findByQnaList(pageable, filter, keyword, toDate, fromDate);
    }

    // QnA삭제
    @Transactional
    public void deleteQnaList(List<Long> deleteList, String loginId) {
        isAdmin(loginId);
        // QnA id가 있으면 삭제
        for (Long deleteId : deleteList) {
            if(qnaRepository.existsById(deleteId)) {
                qnaRepository.deleteById(deleteId);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    // QnA 상세 조회
    public QnADto findQna(Long qnaId) {
        QnA findQna = qnaRepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);
        return QnADto.fromEntity(findQna);
    }

    // QnA 답변 등록 / 업데이트
    @Transactional
    public void modifyQnaAnswer(Long qnaId, String answer, String loginId) {
        QnA findQna = qnaRepository.findById(qnaId).orElseThrow(IllegalArgumentException::new);
        //
        Member answerer = memberRepository.findByLoginId(loginId).orElseThrow(IllegalArgumentException::new);
        // 관리자인지 확인
        if(!answerer.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
        // 답글이 존재하지 않으면 새로 등록
        if(findQna.getQnAAnswer() == null) {
            qnAAnswerRepository.save(QnAAnswer.builder()
                    .answer(answer)
                    .qna(findQna)
                    .member(answerer)
                    .build());
        } else {
            // 답글이 이미 등록되어 있지만 답변을 변경하고 싶으면 답변을 수정하도록 함
            findQna.getQnAAnswer().changeAnswer(answer, answerer);
        }
    }
}
