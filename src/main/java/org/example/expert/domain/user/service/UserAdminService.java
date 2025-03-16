package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.log.repository.LogRepository;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final LogService logService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUserRole(AuthUser authUser, long userId, UserRoleChangeRequest userRoleChangeRequest) {
        User requestUser = User.fromAuthUser(authUser);
        logService.saveLog(requestUser.getId(), LocalDateTime.now());
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        user.updateRole(UserRole.of(userRoleChangeRequest.getRole()));
    }
}
