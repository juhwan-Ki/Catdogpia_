package com.likelion.catdogpia.controller;

import com.likelion.catdogpia.domain.dto.admin.OrderDto;
import com.likelion.catdogpia.domain.dto.admin.OrderStatusUpdateDto;
import com.likelion.catdogpia.domain.entity.product.OrderStatus;
import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderManagementController {

    private final OrderManagementService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    // 주문관리 목록
    @GetMapping()
    public String orderList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String orderStatus
    ) {
        log.info("filter : " + filter);
        log.info("keyword : " + keyword);
        log.info("toDate : " + toDate);
        log.info("fromDate : " + fromDate);
        log.info("orderStatus : " + orderStatus);
        model.addAttribute("orderList", orderService.findOrderList(pageable, filter, keyword, toDate, fromDate, orderStatus));
        model.addAttribute("filter",filter);
        model.addAttribute("keyword",keyword);
        model.addAttribute("toDate",toDate);
        model.addAttribute("fromDate",fromDate);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("orderStatusList", Arrays.asList(OrderStatus.values()));
        return "page/admin/orders";
    }

    // 주문 상태 변경
    @PostMapping("/change-status")
    @ResponseBody
    public ResponseEntity<String> changeOrderStatus(@RequestHeader("Authorization") String accessToken, @RequestBody List<OrderStatusUpdateDto> updateDtoList) {
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        // 백단에서 한번 더 list validation check 수행
        if(updateDtoList.isEmpty()) {
            throw new IllegalArgumentException("list is empty");
        }
        else {
            orderService.changeOrderStatus(updateDtoList, loginId);
        }

        return ResponseEntity.ok("ok");
    }

    // 주문내역상세 조회 및 수정
    @GetMapping("/{orderId}/modify-form")
    public String orderModifyForm(@PathVariable Long orderId, Model model) {
        List<OrderDto> order = orderService.findOrder(orderId);

        for (OrderDto orderProductDto : order) {
            log.info(orderProductDto.toString());
        }
        // 해당 주문에 대한 정보가 없으면 오류 발생
        if(order.isEmpty()) {
            throw new IllegalArgumentException("list is empty");
        }

        model.addAttribute("orderStatusList", Arrays.asList(OrderStatus.values()));
        model.addAttribute("order", order);

        return "page/admin/order-modify";
    }
}
