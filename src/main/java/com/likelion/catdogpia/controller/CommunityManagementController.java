package com.likelion.catdogpia.controller;

import com.likelion.catdogpia.domain.dto.admin.CommentDto;
import com.likelion.catdogpia.jwt.JwtTokenProvider;
import com.likelion.catdogpia.service.CommunityManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/communities")
public class CommunityManagementController {

    private final CommunityManagementService communityService;
    private final JwtTokenProvider jwtTokenProvider;

    // 커뮤니티 목록
    @GetMapping()
    public String communityList(
            Model model,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword
    ) {
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("communityList", communityService.findCommunityList(pageable, filter, keyword));

        return "page/admin/communities";
    }

    // 커뮤니티 삭제
    @PostMapping("/delete-list")
    @ResponseBody
    public ResponseEntity<String> communitiesDelete(@RequestHeader("Authorization") String accessToken, @RequestBody List<Map<String, Object>> requestList) {
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        // 한번더 체크
        if(requestList.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            List<Long> deleteList = new ArrayList<>();
            // deleteList 생성
            for (Map<String, Object> map : requestList) {
                deleteList.add(Long.valueOf((String) map.get("id")));
            }
            communityService.deleteCommunities(deleteList, loginId);
        }

        return ResponseEntity.ok("ok");
    }

    // 커뮤니티 상세 조회
    @GetMapping("/{communityId}")
    public String communityDetails(
            @PathVariable Long communityId,
            Model model) {
        model.addAttribute("community", communityService.findCommunity(communityId));
        return "page/admin/community-detail";
    }

    // 커뮤니티 댓글 조회
    @GetMapping("/{communityId}/comments")
    @ResponseBody
    public Page<CommentDto> commentList(@PathVariable Long communityId, @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        Page<CommentDto> list = communityService.findCommentList(communityId, pageable);
        log.info("hi : " + list);
        return list;
    }

    // 댓글 삭제
    @PostMapping("/{communityId}/comments/{commentId}")
    @ResponseBody
    public ResponseEntity<String> commentDelete(@RequestHeader("Authorization") String accessToken,  @PathVariable Long communityId, @PathVariable Long commentId) {
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        communityService.deleteComment(communityId, commentId, loginId);
        return  ResponseEntity.ok("ok");
    }

    // 댓글 등록
    @PostMapping("/{communityId}/comments/create")
    public ResponseEntity<String> commentCreate(@RequestHeader("Authorization") String accessToken,  @PathVariable Long communityId, @RequestBody String content) {
        if(accessToken == null){
            throw new RuntimeException();
        }
        // 토큰에서 loginId 가져옴
        String token = accessToken.split(" ")[1];
        String loginId = jwtTokenProvider.parseClaims(token).getSubject();
        communityService.createComment(communityId, content, loginId);
        return ResponseEntity.ok("ok");
    }
}
