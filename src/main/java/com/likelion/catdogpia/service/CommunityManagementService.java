package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.admin.CommentDto;
import com.likelion.catdogpia.domain.dto.admin.CommunityDto;
import com.likelion.catdogpia.domain.dto.admin.CommunityListDto;
import com.likelion.catdogpia.domain.entity.community.Article;
import com.likelion.catdogpia.domain.entity.community.Comment;
import com.likelion.catdogpia.domain.entity.user.Member;
import com.likelion.catdogpia.repository.*;
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
public class CommunityManagementService {

    // 커뮤니티 관련
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final AttachDetailRepository attachDetailRepository;
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

    // 커뮤니티 목록 조회
    public Page<CommunityListDto> findCommunityList(Pageable pageable, String filter, String keyword) {
        return queryRepository.findByCommunityList(pageable, filter, keyword);
    }

    // 커뮤니티 삭제
    @Transactional
    public void deleteCommunities(List<Long> deleteList, String loginId) {
        // 관리자인지 확인
        isAdmin(loginId);
        // 커뮤니티 id가 있으면 삭제
        for (Long deleteId : deleteList) {
            if(communityRepository.existsById(deleteId)) {
                communityRepository.deleteById(deleteId);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    // 커뮤니티 상세
    public CommunityDto findCommunity(Long communityId) {
        Article findArticle = communityRepository.findById(communityId).orElseThrow(IllegalArgumentException::new);
        // 커뮤니티 조회
        CommunityDto community = CommunityDto.fromEntity(findArticle);
        // 파일이 있으면 fileUrl 조회해서 넘김
        if(findArticle.getAttach() != null) {
            List<String> findList = attachDetailRepository.findFileUrls(findArticle.getAttach());
            community.setFileUrlList(findList);
        }
        return community;
    }

    // 댓글 조회
    public Page<CommentDto> findCommentList(Long communityId, Pageable pageable) {
        Article findArticle = communityRepository.findById(communityId).orElseThrow(IllegalArgumentException::new);
        // 댓글이 있으면 페이지 네이션
        if(!findArticle.getCommentList().isEmpty()) {
            return commentRepository.findByArticle(findArticle, pageable)
                    .map(c -> CommentDto.builder()
                            .id(c.getId())
                            .content(c.getContent())
                            .commentWriter(c.getMember().getName())
                            .createdAt(c.getCreatedAt())
                            .build());

        } else return null;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long communityId, Long commentId, String loginId) {
        // 관리자 권한 체크
        isAdmin(loginId);

        if(commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 댓글 등록
    @Transactional
    public void createComment(Long communityId, String content, String loginId) {

        Article findArticle = communityRepository.findById(communityId).orElseThrow(IllegalArgumentException::new);
        // 권한에서 사용자에 대한 정보가져오기
        Member findMember = memberRepository.findByLoginId(loginId).orElseThrow(IllegalArgumentException::new);
        // 관리자인지 확인
        if(!findMember.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }

        commentRepository.save(Comment.builder()
                .article(findArticle)
                .content(content)
                .member(findMember)
                .build());
    }

}
