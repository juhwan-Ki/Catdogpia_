package com.likelion.catdogpia.domain.entity.community;

import com.likelion.catdogpia.domain.dto.admin.ProductDto;
import com.likelion.catdogpia.domain.dto.community.ArticleDto;
import com.likelion.catdogpia.domain.entity.BaseEntity;
import com.likelion.catdogpia.domain.entity.CategoryEntity;
import com.likelion.catdogpia.domain.entity.attach.Attach;
import com.likelion.catdogpia.domain.entity.report.Report;
import com.likelion.catdogpia.domain.entity.user.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Entity
@SQLDelete(sql = "UPDATE article SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at is null")
public class Article extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCnt;

    //카테고리 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    //댓글 연관관계
    @OneToMany(mappedBy = "article")
    private List<Comment> commentList = new ArrayList<>();

    //파일 연관관계
    @OneToOne
    @JoinColumn(name = "attach_id")
    private Attach attach;

    //회원 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //신고 연관관계
    @OneToMany(mappedBy = "article")
    private List<Report> reportList = new ArrayList<>();

    //좋아요 연관관계
    @OneToMany(mappedBy = "article")
    private List<LikeArticle> likeArticles = new ArrayList<>();

    @Builder
    public Article(Long id, String title, String content, int viewCnt, CategoryEntity category, List<Comment> commentList, Attach attach, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewCnt = viewCnt;
        this.category = category;
        this.commentList = commentList;
        this.attach = attach;
        this.member = member;
    }

    // 글 수정
    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void updateCategory(CategoryEntity newCategory) {
        this.category = newCategory;
    }
}
