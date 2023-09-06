package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.mypage.ReviewFormDto;
import com.likelion.catdogpia.domain.dto.mypage.ReviewListDto;
import com.likelion.catdogpia.domain.dto.mypage.ReviewProductDto;
import com.likelion.catdogpia.domain.entity.attach.Attach;
import com.likelion.catdogpia.domain.entity.attach.AttachDetail;
import com.likelion.catdogpia.domain.entity.product.OrderProduct;
import com.likelion.catdogpia.domain.entity.review.Review;
import com.likelion.catdogpia.domain.entity.user.Member;
import com.likelion.catdogpia.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final AttachRepository attachRepository;
    private final AttachDetailRepository attachDetailRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderRespository orderRespository;

    private final S3UploadService s3UploadService;

    // 리뷰 내역 조회
    @Transactional(readOnly = true)
    public Page<ReviewListDto> findAllReview(String loginId, Integer page) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<ReviewListDto> reviewListPage = reviewRepository.findAllByMemberId(pageable, member.getId());

        return reviewListPage;
    }

    // 리뷰 작성 페이지
    public ReviewProductDto getOrderProduct(String loginId, Long opId) {

        return orderProductRepository.findProductByOrderProductId(opId);
    }

    // 리뷰 등록
    public void saveReview(String loginId, Long opId, MultipartFile reviewImg, ReviewFormDto reviewFormDto) throws IOException {

        // 상품 구매자와 일치하는지 확인
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        OrderProduct orderProduct = orderProductRepository.findById(opId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long orderMemberId = orderRespository.findMemberIdByOrderProductId(opId);
        if(orderMemberId != member.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "member 불일치");
        }

        // 이미 등록한 리뷰가 있는지 확인
        if(reviewRepository.findByOrderProductId(opId) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록한 리뷰 존재");
        }
        
        Attach attach = null;
        // (첨부 이미지가 있다면) 리뷰 이미지 S3로 업로드
        if (reviewImg != null) {
            attach = attachRepository.save(Attach.builder().createdAt(LocalDateTime.now()).build());
            String mainImgFileUrl = s3UploadService.upload(reviewImg, "review");
            attachDetailRepository.save(AttachDetail.builder()
                    .attach(attach)
                    .fileSize(reviewImg.getSize())
                    .realname(reviewImg.getOriginalFilename())
                    .fileUrl(mainImgFileUrl)
                    .build());
            log.info("리뷰 이미지 저장 완료");
        }

        // 리뷰 등록
        reviewRepository.save(Review.builder()
                .member(member)
                .orderProduct(orderProduct)
                .attach(attach)
                .description(reviewFormDto.getDescription())
                .rating(reviewFormDto.getRating())
                .build());
        log.info("리뷰 등록 완료");

        // TODO 포인트 적립
    }

}
