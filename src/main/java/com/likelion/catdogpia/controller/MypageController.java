package com.likelion.catdogpia.controller;


import com.likelion.catdogpia.domain.dto.mypage.*;
import com.likelion.catdogpia.domain.entity.product.OrderStatus;
import com.likelion.catdogpia.service.AddressService;
import com.likelion.catdogpia.service.OrderHistoryService;
import com.likelion.catdogpia.service.PointService;
import com.likelion.catdogpia.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final AddressService addressService;
    private final OrderHistoryService orderHistoryService;
    private final PointService pointService;
    private final ReviewService reviewService;

    // 프로필 페이지
    @GetMapping("")
    public String profilePage(Model model) {
        return "page/mypage/profile.html";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/edit-profile")
    public String editProfilePage(Model model) {
        return "page/mypage/edit_profile.html";
    }

    // 반려동물 등록 페이지
    @GetMapping("/edit-pet")
    public String addPetPage(Model model) {
        return "page/mypage/add_pet.html";
    }

    // 반려동물 등록
    @PostMapping("/edit-pet")
    public String addPet(Model model) {
        return "page/mypage/add_pet.html";
    }

    // 주문 내역 페이지
    @GetMapping("/order-list")
    public String orderListPage(Model model, @RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus, @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Page<OrderListDto> orderList = orderHistoryService.readAllOrder("testtest", orderStatus, page);
        model.addAttribute("orderList", orderList);
        // 주문 상태별 개수
        model.addAttribute("orderStatusCount", orderHistoryService.getOrderCountByStatus("testtest"));
        return "page/mypage/order_list.html";
    }

    // 주문 내역 > 구매 확정
    @PutMapping("/order-list/purchase-confirm/{opId}")
    @ResponseBody
    public ResponseDto purchaseConfirm(@PathVariable Long opId) {
        orderHistoryService.purchaseConfirm("testtest", opId);
        return new ResponseDto("구매 확정 되었습니다.");
    }

    // 교환 요청 페이지
    @GetMapping("/order-list/exchange/{opId}")
    public String exchangePage(@PathVariable Long opId, Model model) {
        model.addAttribute("order", orderHistoryService.exchangeRefund("testtest", opId));
        model.addAttribute("option", orderHistoryService.getProductOption(opId));
        return "page/mypage/exchange.html";
    }

    // 환불 요청 페이지
    @GetMapping("/order-list/refund/{opId}")
    public String refundPage(@PathVariable Long opId, Model model) {
        model.addAttribute("order", orderHistoryService.exchangeRefund("testtest", opId));
        return "page/mypage/refund.html";
    }

    // 주문 상세 페이지
    @GetMapping("/order-detail/{orderId}")
    public String orderDetailPage(Model model, @PathVariable Long orderId, @RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        model.addAttribute("productList", orderHistoryService.readOrder("testtest", orderId, page, limit));
        model.addAttribute("productDetail", orderHistoryService.readDetail("testtest", orderId));
        return "page/mypage/order_detail.html";
    }

    // 적립금 페이지
    @GetMapping("/point")
    public String pointPage(Model model, @RequestParam(value = "page", defaultValue = "0") Integer page) {
        // 적립금 내역
        model.addAttribute("pointList", pointService.findAllPoint("testtest", page));
        return "page/mypage/point.html";
    }

    // 배송지 관리 페이지
    @GetMapping("/address")
    public String addressPage(Model model, @RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        model.addAttribute("addressList", addressService.readAllAddress("testtest", page, limit));
        return "page/mypage/address.html";
    }

    // 배송지 등록 페이지
    @GetMapping("/add-address")
    public String addAddressPage(Model model) {
        return "page/mypage/add_address.html";
    }

    // 배송지 등록
    @PostMapping("/add-address")
    public String postAddAddress(AddressFormDto dto) {
        addressService.saveAddress("testtest", dto);
        return "redirect:/mypage/address";
    }

    // 배송지 수정 팝업 페이지
    @GetMapping("/address/update/{addressId}")
    public String updateAddressPage(@PathVariable Long addressId, Model model) {
        model.addAttribute("address", addressService.readAddress(addressId));
        return "page/mypage/edit_address.html";
    }

    // 배송지 수정
    @PostMapping("/address/update/{addressId}")
    public String updateAddress(@PathVariable Long addressId, AddressFormDto dto) {
        addressService.updateAddress(addressId, dto);
        return "redirect:/mypage/address";
    }

    @PostMapping("/address/delete/{addressId}")
    public String deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress("testtest", addressId);
        return "redirect:/mypage/address";
    }

    // 리뷰 관리 페이지
    @GetMapping("/review")
    public String reviewPage(Model model, @RequestParam(value = "page", defaultValue = "0") Integer page) {
        model.addAttribute("reviewList", reviewService.findAllReview("testtest", page));
        return "page/mypage/review.html";
    }

    // 게시글 관리 페이지
    @GetMapping("/article")
    public String mypagePage(Model model) {
        return "page/mypage/article.html";
    }

}
