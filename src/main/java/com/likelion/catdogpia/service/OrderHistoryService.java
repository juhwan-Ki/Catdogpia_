package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.mypage.*;
import com.likelion.catdogpia.domain.entity.mypage.ExchangeRefund;
import com.likelion.catdogpia.domain.entity.order.Orders;
import com.likelion.catdogpia.domain.entity.product.OrderProduct;
import com.likelion.catdogpia.domain.entity.product.OrderStatus;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderRespository orderRespository;
    private final MemberRepository memberRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ExchangeRefundRepository exchangeRefundRepository;

    // 주문 내역 리스트 조회
    public Page<OrderListDto> readAllOrder(String loginId, OrderStatus orderStatus, Integer page) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<OrderListDto> orderListPage = orderRespository.findAllByMemberId(pageable, member.getId(), orderStatus);

        return orderListPage;
    }

    // 주문 상세 조회 > 특정 주문 번호의 상품들 조회
    public Page<OrderListDto> readOrder(String loginId, Long orderId, Integer page, Integer limit) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<OrderListDto> orderListPage = orderRespository.findAllByOrderId(pageable, orderId);

        return orderListPage;
    }

    // 주문 상세 조회 > 배송지 정보, 결제 정보
    public OrderDetailDto readDetail(String loginId, Long orderId) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);
        if (optionalMember.isEmpty()) {
            new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Optional<Orders> optionalOrders = orderRespository.findById(orderId);
        if (optionalOrders.isEmpty()) {
            new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return orderRespository.findDetailByOrderId(orderId);
    }

    // 주문 상태별 개수
    public Map<String, Integer> getOrderCountByStatus(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Map<String, Integer> map = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            int count = orderProductRepository.countOrderStatus(member.getId(), status);
            map.put(String.valueOf(status), count);
        }
        return map;
    }

    // 구매 확정
    @Transactional
    public void purchaseConfirm(String loginId, Long opId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        OrderProduct op = orderProductRepository.findById(opId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!(op.getOrderStatus().equals("배송중") || op.getOrderStatus().equals("배송완료"))) {
            new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        op.changeOrderStatus(OrderStatus.valueOf("PURCHASE_CONFIRMED"));
        op.changePurchaseConfirmAt();
    }

    // 교환,환불 요청 페이지 - 주문 정보
    public ExchangeRefundDto getOrderInfo(String loginId, Long opId) {
        return orderProductRepository.findByOrderProductId(opId);
    }

    // 교환 페이지 - 해당 상품 옵션 목록
    public Map<String, List<String>> getProductOption(Long opId) {

        // 해당 상품 조회
        Long pId = productRepository.findProductId(opId);

        List<String> sizeList = productOptionRepository.findSizeByProductIdAndSizeIsNotNull(pId);
        List<String> colorList = productOptionRepository.findSizeByProductIdAndColorIsNotNull(pId);

        // *재고 확인 필요

        Map<String, List<String>> response = new HashMap<>();
        response.put("size", sizeList);
        response.put("color", colorList);

        return response;
    }

    // 교환 요청 처리
    @Transactional
    public void exchange(String loginId, ExchangeRequestDto dto) {

        // 주문 상품
        OrderProduct op = orderProductRepository.findById(dto.getOpId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 주문한 사람과 현재 로그인한 사람이 일치하는지 확인
        Orders order = orderRespository.findById(op.getOrder().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (order.getMember().getLoginId() != loginId) {
            new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        ExchangeRefund exchangeRefund = dto.toEntity(op);

        exchangeRefundRepository.save(exchangeRefund);

        // 주문 상태 변경
        op.changeOrderStatus(OrderStatus.EXCHANGE_REQUESTED);

    }

    // 환불 요청 처리
    @Transactional
    public void refund(String loginId, RefundRequestDto dto) {

        // 주문 상품
        OrderProduct op = orderProductRepository.findById(dto.getOpId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 주문한 사람과 현재 로그인한 사람이 일치하는지 확인
        Orders order = orderRespository.findById(op.getOrder().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (order.getMember().getLoginId() != loginId) {
            new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        ExchangeRefund exchangeRefund = dto.toEntity(op);

        exchangeRefundRepository.save(exchangeRefund);

        // 주문 상태 변경
        op.changeOrderStatus(OrderStatus.REFUND_REQUESTED);

    }

}