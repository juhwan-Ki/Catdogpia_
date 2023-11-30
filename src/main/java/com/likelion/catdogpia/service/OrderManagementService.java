package com.likelion.catdogpia.service;

import com.likelion.catdogpia.domain.dto.admin.OrderDto;
import com.likelion.catdogpia.domain.dto.admin.OrderListDto;
import com.likelion.catdogpia.domain.dto.admin.OrderStatusUpdateDto;
import com.likelion.catdogpia.domain.entity.order.Orders;
import com.likelion.catdogpia.domain.entity.product.OrderProduct;
import com.likelion.catdogpia.domain.entity.product.OrderStatus;
import com.likelion.catdogpia.domain.entity.user.Member;
import com.likelion.catdogpia.repository.MemberRepository;
import com.likelion.catdogpia.repository.OrderProductRepository;
import com.likelion.catdogpia.repository.OrderRepository;
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
public class OrderManagementService {

    // 주문관련
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
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

    // 주문 목록
    public Page<OrderListDto> findOrderList(Pageable pageable, String filter, String keyword, String toDate, String fromDate, String orderStatus) {
        return queryRepository.findByOrderList(pageable, filter, keyword, toDate, fromDate, orderStatus != null ? OrderStatus.valueOf(orderStatus) : null);
    }

    // 주문 상태 변경
    @Transactional
    public void changeOrderStatus(List<OrderStatusUpdateDto> updateDtoList, String loginId) {
        // 권한체크
        isAdmin(loginId);
        for (OrderStatusUpdateDto updateDto : updateDtoList) {
            // id를 가지고 해당 주문 상품을 찾음
            OrderProduct findOrderProduct = orderProductRepository.findById(updateDto.getId()).orElseThrow(IllegalArgumentException::new);
            // 이미 같으면 update 하지 않고 스킵
            if(findOrderProduct.getOrderStatus().name().equals(updateDto.getStatus())) continue;
            else {
                findOrderProduct.changeStatus(updateDto.getStatus());
            }
        }
    }

    // 주문내역 조회
    public List<OrderDto> findOrder(Long orderId) {
        // 해당 주문 내역이 있는지 확인 조회
        Orders orders = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);

        // 주문 목록 리턴
        return queryRepository.findOrder(orderId);
    }


}
