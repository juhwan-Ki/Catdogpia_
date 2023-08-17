package com.likelion.catdogpia.domain.entity.attach;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AttachDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private Integer fileSize;

    @Column(nullable = false, length = 260)
    private String realname;

    @Builder
    public AttachDetail(Long id, String fileUrl, Integer fileSize, String realname) {
        this.id = id;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.realname = realname;
    }
}