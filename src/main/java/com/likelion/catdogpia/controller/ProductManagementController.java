package com.likelion.catdogpia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.catdogpia.domain.dto.admin.CategoryDto;
import com.likelion.catdogpia.domain.dto.admin.ProductDto;
import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.MemberManagementService;
import com.likelion.catdogpia.service.ProductManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductManagementController {

    private final ProductManagementService productService;
    private final JwtTokenProvider jwtTokenProvider;

    // 상품 목록
    @GetMapping()
    public String productList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword
    ) {
        model.addAttribute("productList", productService.findProductList(pageable, filter, keyword));
        model.addAttribute("filter",filter);
        model.addAttribute("keyword",keyword);
        return "page/admin/products";
    }

    // 상품 등록 페이지
    @GetMapping("/create-form")
    public String productCreateForm(Model model) {
        List<CategoryDto> categoryList = productService.findCategory();
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("productDto", new ProductDto());
        return "page/admin/product-create";
    }

    // 상품 등록
    @PostMapping(value = "/create")
    @ResponseBody
    public ResponseEntity<String> productCreate(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("mainImg") MultipartFile mainImg,
            @RequestParam("detailImg") MultipartFile detailImg,
            @RequestParam("productDto") String productDto
    ) throws IOException {

        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();

        log.info("product : " + productDto);
        log.info("mainImg :" + mainImg.getOriginalFilename());
        log.info("detailImg :" + detailImg.getOriginalFilename());
        // json -> productDto
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDto product = objectMapper.readValue(productDto, ProductDto.class);
        log.info("product toString : " + product.toString());

        productService.createProduct(product, mainImg, detailImg, loginId);

        return ResponseEntity.ok("ok");
    }

    // 상품 수정 페이지
    @GetMapping("/{productId}/modify-form")
    public String productModifyForm(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        model.addAttribute("productDto", productService.findProduct(productId));
        model.addAttribute("categoryList", productService.findCategory());
        log.info("product : " + productService.findProduct(productId).toString());
        return "page/admin/product-modify";
    }

    // 상품 수정
    @PostMapping("/{productId}/modify")
    public ResponseEntity<String> productModify(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long productId,
            @RequestParam(value = "mainImg", required = false) MultipartFile mainImg,
            @RequestParam(value = "detailImg", required = false) MultipartFile detailImg,
            @RequestParam("productDto") String productDto
    ) throws IOException {

        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();

        log.info("productDto : " + productDto);
        // json -> productDto
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDto product = objectMapper.readValue(productDto, ProductDto.class);
        log.info("product toString : " + product.toString());

        productService.modifyProduct(productId, product, mainImg, detailImg, loginId);

        return ResponseEntity.ok("ok");
    }

    // 상품 삭제
    @PostMapping("/{productId}/delete")
    @ResponseBody
    public ResponseEntity<String> productRemove(@RequestHeader("Authorization") String accessToken, @PathVariable Long productId){
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();

        productService.removeProduct(productId, loginId);
        return ResponseEntity.ok("ok");
    }

    // 상품명 중복 확인
    @GetMapping("/duplicate-check")
    @ResponseBody
    public Map<String, Boolean> checkProductNameAvailability(
            @RequestParam("name") String name,
            @RequestParam(required = false) Long productId
    ){
        Map<String, Boolean> resultMap = new HashMap<>();
        resultMap.put("duplicated", productService.isDuplicatedProductName(name, productId));
        log.info("resultMap : " + resultMap.get("duplicated"));
        return resultMap;
    }
}
