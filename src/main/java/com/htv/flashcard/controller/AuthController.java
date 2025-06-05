package com.htv.flashcard.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.htv.flashcard.DTO.AuthResponse;
import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.DTO.LoginRequest;
import com.htv.flashcard.DTO.UserDTO;
import com.htv.flashcard.DTO.UserProfileDTO;
import com.htv.flashcard.model.Status;
import com.htv.flashcard.model.User;
import com.htv.flashcard.security.JwtUtil;
import com.htv.flashcard.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Đăng ký tài khoản: kiểm tra email, mã hóa mật khẩu, lưu user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {

        // 2) Kiểm tra email đã tồn tại
        if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                                .body("Email đã tồn tại!");
        }

        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        userService.registerUser(user);
        return ResponseEntity.ok("Đăng ký thành công");
    }

    /**
     * Đăng nhập: xác thực, trả về JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (UsernameNotFoundException | org.springframework.security.authentication.BadCredentialsException ex) {
            return ResponseEntity
                   .status(HttpStatus.UNAUTHORIZED)
                   .body(Map.of("message", "Email hoặc mật khẩu không đúng"));
        }
            
        String token = jwtUtil.generateToken(request.getEmail());
        User u = userService.findByEmail(request.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));
        String role = u.getRole().name();  // "ADMIN" hoặc "USER"
        return ResponseEntity.ok(new AuthResponse(token, role));
    }

    /**
     * Cập nhật user hiện tại, có thể bao gồm upload avatar
     */
    @PutMapping(
      value = "/user/{id}", 
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateUser(
        @PathVariable Long id,
        @RequestPart("fullName") String fullName,
        @RequestPart(value = "avatar", required = false) MultipartFile avatar,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 1. Kiểm tra quyền
        User current = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        if (!current.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Bạn không có quyền cập nhật user này");
        }

        // 2. Thực hiện cập nhật
        UserProfileDTO updated = userService.updateUserProfile(id, fullName, avatar);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa chính user đang login. Nếu id path khác -> 403.
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User current = userService.findByEmail(userDetails.getUsername())
                                  .orElseThrow();

        if (!current.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Bạn không có quyền xóa user này");
        }

        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa người dùng thành công");
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        User u = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        // Mới: map id, title, description và flashcardCount
        List<FlashcardSetDTO> sets = u.getFlashcardSets().stream()
            .map(fs -> {
                    int total = fs.getFlashcards().size();
                    long learned = fs.getFlashcards().stream()
                                    .filter(f -> f.getStatus() == Status.LEARNED)
                                    .count();
                    double percent = total > 0 ? (learned * 100.0 / total) : 0.0;
                    // 2. Tính điểm đánh giá trung bình
                    double avgRating = fs.getRatings() != null
                        ? fs.getRatings().stream()
                            .mapToInt(r -> r.getScore())
                            .average()
                            .orElse(0.0)
                        : 0.0;
                    return new FlashcardSetDTO(
                    fs.getId(),
                    fs.getTitle(),
                    fs.getDescription(),
                    fs.getLastStudiedAt() != null ? fs.getLastStudiedAt() : fs.getCreatedAt(),
                    fs.getSavedByUsers().size(),
                    fs.getUser().getId(),
                    fs.getUser().getFullName(),
                    fs.getUser().getAvatarUrl(),
                    total,
                    percent,
                    avgRating 
                );
            })
            .collect(Collectors.toList());
        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(u.getId());  
        profile.setFullName(u.getFullName());
        profile.setEmail(u.getEmail());
        profile.setFlashcardSets(sets);
        profile.setAvatarUrl(u.getAvatarUrl());
        return ResponseEntity.ok(profile);
    }
} 
