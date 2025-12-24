package com.demo.TaskManager.services;

import com.demo.TaskManager.common.exceptions.BadRequestException;
import com.demo.TaskManager.common.exceptions.EmailAlreadyExistsException;
import com.demo.TaskManager.common.exceptions.UserNotFoundException;
import com.demo.TaskManager.dtos.LoginRequest;
import com.demo.TaskManager.dtos.RegisterRequest;
import com.demo.TaskManager.dtos.UserInfoResponse;
import com.demo.TaskManager.security.JwtService;
import com.demo.TaskManager.entities.User;
import com.demo.TaskManager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        log.info("[AUTH] Registration attempt for email: {}", dto.email());

        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            log.warn("[AUTH] Registration failed: Email already exists: {}", dto.email());
            throw new EmailAlreadyExistsException("Email déjà utilisé");
        }

        User user = AuthMapper.fromDTO(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        log.info("[AUTH] User registered successfully: id={}, email={}, name={}",
            user.getId(), user.getEmail(), user.getName());

        String token = login(AuthMapper.fromDto(dto));
        log.info("[AUTH] User auto-logged in after registration: {}", dto.email());

        return token;
    }

    /**
     * Authentifie un utilisateur et génère un token JWT
     * @param loginRequest les credentials de l'utilisateur
     * @return le token JWT
     * @throws BadCredentialsException si les credentials sont invalides
     */
    public String login(LoginRequest loginRequest) {
        log.info("[AUTH] Login attempt for email: {}", loginRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        log.info("[AUTH] User authenticated successfully: {}", authentication.getName());

        String token = tokenService.generateToken(authentication);
        log.debug("[AUTH] JWT token generated for user: {}", authentication.getName());

        return token;
    }

    /**
     * Récupère les informations de l'utilisateur connecté depuis le token JWT
     * @param token le token JWT (sans le préfixe "Bearer ")
     * @return UserInfoResponse contenant les informations de l'utilisateur
     */
    public UserInfoResponse getCurrentUserInfo(String token) {
        log.debug("[AUTH] Fetching current user info from token");

        // Valider le token
        if (!tokenService.validateToken(token)) {
            log.warn("[AUTH] Invalid JWT token provided");
            throw new BadRequestException("Token JWT invalide");
        }

        // Extraire l'ID utilisateur du token
        Long userId = tokenService.getUserIdFromToken(token);
        log.debug("[AUTH] User ID extracted from token: {}", userId);

        // Récupérer l'utilisateur complet depuis la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[AUTH] User not found with ID from token: {}", userId);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId);
                });

        log.info("[AUTH] Current user info retrieved: id={}, email={}", user.getId(), user.getEmail());

        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}



