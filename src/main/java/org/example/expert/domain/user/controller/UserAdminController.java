package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserAdminService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @Secured(UserRole.Authority.ADMIN)
    @PatchMapping("/admin/users/{userId}")
    public void changeUserRole( @AuthenticationPrincipal AuthUser authUser,
                                @PathVariable long userId,
                                @RequestBody UserRoleChangeRequest userRoleChangeRequest) {
        User requestUser = User.fromAuthUser(authUser);
        if (requestUser.getUserRole() != UserRole.ROLE_ADMIN) {
            throw new InvalidRequestException("admin 권한이 없습니다.");
        }
        userAdminService.changeUserRole(authUser, userId, userRoleChangeRequest);
    }
}
