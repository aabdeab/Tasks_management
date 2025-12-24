package com.demo.TaskManager.services;

import com.demo.TaskManager.common.exceptions.ResourceNotFoundException;
import com.demo.TaskManager.dtos.LoginRequest;
import com.demo.TaskManager.dtos.RegisterRequest;
import com.demo.TaskManager.dtos.UserInfoResponse;
import com.demo.TaskManager.security.JwtService;
import com.demo.TaskManager.security.SecurityUser;

import com.demo.TaskManager.entities.User;
import com.demo.TaskManager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.demo.TaskManager.mappers.AuthMapper;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService tokenService;

    @Transactional
    public String registerUser(RegisterRequest dto) {
        User user = AuthMapper.fromDTO(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
        log.info("[USER] : User created with id {}", user.getId());
        return login(AuthMapper.fromDto(dto));
    }
    /**
     *
     * @param loginRequest
     * @return
     * @Throws BadCredentialsException si le le mot de passe ou email ne sont pas corrects
     */
    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        log.info("[USER] : User Authenticated with email {}",authentication.getName());
        return tokenService.generateToken(authentication);
    }

    /**
     * Récupère les informations de l'utilisateur connecté
     * @return UserInfoResponse contenant les informations de l'utilisateur
     * @throws ResourceNotFoundException si l'utilisateur n'est pas trouvé
     */
    public UserInfoResponse getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Utilisateur non authentifié");
        }

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.user();

        log.info("[USER] : Fetching current user info for user with id {}", user.getId());

        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

}


