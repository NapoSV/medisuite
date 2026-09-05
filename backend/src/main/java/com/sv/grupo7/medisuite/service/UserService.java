package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dto.user.UserMeResponse;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserMeResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        return new UserMeResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCif(),
                user.getRole(),
                user.getTenantId());
    }
}
