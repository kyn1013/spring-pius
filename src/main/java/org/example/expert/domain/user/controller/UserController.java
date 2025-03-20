package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserProfileResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserProfileResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @GetMapping("/users")
    public ResponseEntity<UserProfileResponse> searchUser(@RequestParam String nickName) {
        return ResponseEntity.ok(userService.searchUser(nickName));
    }

    // 사용자 프로필 이미지 업로드
    @PostMapping("/users/image")
    public ResponseEntity<UserProfileResponse> uploadImage(@AuthenticationPrincipal AuthUser authUser, @RequestPart MultipartFile image) throws IOException {
        UserProfileResponse user = userService.uploadImage(authUser.getId(), image);
        return ResponseEntity.ok(user);
    }

    // 사용자 프로필 이미지 삭제
    @DeleteMapping("/users/image")
    public void imageDelete(@AuthenticationPrincipal AuthUser authUser, @RequestParam String imageUrl) {
        userService.deleteImage(authUser.getId(), imageUrl);
    }


}
