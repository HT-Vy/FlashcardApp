package com.htv.flashcard.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htv.flashcard.DTO.AuthResponse;
import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.DTO.LoginRequest;
import com.htv.flashcard.DTO.UserDTO;
import com.htv.flashcard.DTO.UserProfileDTO;
import com.htv.flashcard.model.User;
import com.htv.flashcard.security.JwtUtil;
import com.htv.flashcard.service.UserService;

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
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
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
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtUtil.generateToken(request.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Cập nhật thông tin user
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    /**
     * Xóa user
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa người dùng thành công");
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        User u = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<FlashcardSetDTO> sets = u.getFlashcardSets().stream().map(fs -> {
            FlashcardSetDTO dto = new FlashcardSetDTO();
            dto.setTitle(fs.getTitle());
            dto.setDescription(fs.getDescription());
            return dto;
        }).collect(Collectors.toList());
        UserProfileDTO profile = new UserProfileDTO();
        profile.setFullName(u.getFullName());
        profile.setEmail(u.getEmail());
        profile.setFlashcardSets(sets);
        return ResponseEntity.ok(profile);
    }
}
