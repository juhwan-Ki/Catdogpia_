package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.admin.MemberDto;
import com.likelion.catdogpia.domain.dto.admin.MemberListDto;
import com.likelion.catdogpia.domain.entity.user.Member;
import com.likelion.catdogpia.repository.MemberRepository;
import com.likelion.catdogpia.repository.QueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberManagementService {

    // 회원관련
    private final MemberRepository memberRepository;
    // 공통
    private final QueryRepository queryRepository;
    //private final AdminCheck adminCheck;

    // 관리자인지 확인하는 메소드
    private void isAdmin(String loginId) {
        Member requestMember = memberRepository.findByLoginId(loginId).orElseThrow(IllegalArgumentException::new);
        // 관리자인지 확인
        if(!requestMember.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
    }

    // 회원관리 목록
    public Page<MemberListDto> findMemberList(Pageable pageable, String filter, String keyword) {
        // 목록 리턴
        return queryRepository.findByFilterAndKeyword(pageable, filter, keyword);
    }

    // 사용자 상세 조회
    public MemberDto findMember(Long memberId) {
        return memberRepository.findByMember(memberId);
    }

    // 사용자 정보 변경
    @Transactional
    public void modifyMember(MemberDto memberDto, Long memberId, String loginId) {
        // 관리자인지 확인
        isAdmin(loginId);
        // 회원 수정
        Member findMember = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        findMember.changeMember(memberDto);
    }

    // 이메일, 닉네임 사용여부 확인
    public Boolean isDuplicated(String email,String nickname, Long memberId) {
        return memberRepository.findByEmailOrNicknameAndIdNot(email, nickname, memberId).isEmpty();
    }

    // 회원 삭제
    @Transactional
    public void removeMember(Long memberId, String loginId) {
        // 관리자인지 확인용
        isAdmin(loginId);
        // 회원 삭제
        Member findMember = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        memberRepository.delete(findMember);
    }
}
