package com.likelion.catdogpia.repository;

import com.likelion.catdogpia.domain.dto.community.ArticleListDto;
import com.likelion.catdogpia.domain.dto.community.HotArticleListDto;
import com.likelion.catdogpia.domain.dto.mypage.MemberArticleListDto;
import com.likelion.catdogpia.domain.entity.community.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    //조회수 count
    @Modifying
    @Query("update Article p set p.viewCnt = p.viewCnt + 1 where p.id = :id")
    int updateViewCnt(@Param("id") Long id);

    //일주일 내에 작성된 글 중 좋아요 수 상위 3개만 조회
    @Query("SELECT new com.likelion.catdogpia.domain.dto.community.HotArticleListDto(a.id, a.title, a.attach, count(la.id) as cnt, a.createdAt)" +
            "FROM Article a " +
            "JOIN LikeArticle la on la.article.id = a.id " +
            "WHERE a.createdAt >= :oneWeekAgo " +
            "GROUP BY a.id, a.title, a.attach, a.createdAt, a.category.id, a.content, a.deletedAt, a.member, a.updatedAt, a.viewCnt, a.attach " +
            "ORDER BY cnt DESC limit 3")
    List<HotArticleListDto> findTop3PopularArticlesWithinOneWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo);

    // 특정 회원 게시물 목록
    @Query("SELECT new com.likelion.catdogpia.domain.dto.mypage.MemberArticleListDto(a.id, ca.name, a.title, COUNT(c), a.viewCnt, COUNT(l), a.createdAt)" +
            "FROM Article a " +
            "JOIN CategoryEntity ca ON ca.id = a.category.id " +
            "LEFT JOIN Comment c ON c.article.id = a.id " +
            "LEFT JOIN LikeArticle l ON l.article.id = a.id " +
            "WHERE a.member.id = :memberId " +
            "GROUP BY a.id, ca.name, a.title, a.viewCnt, a.createdAt")
    Page<MemberArticleListDto> findByMemberId(Pageable pageable, @Param("memberId") Long memberId);
}