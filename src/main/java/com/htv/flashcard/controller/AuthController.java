package com.htv.flashcard.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        // 1) Nếu có lỗi validation, trả lỗi đầu tiên
        // if (bindingResult.hasErrors()) {
        //     String errMsg = bindingResult.getFieldError().getDefaultMessage();
        //     return ResponseEntity.badRequest().body(errMsg);
        // }

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
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtUtil.generateToken(request.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Cập nhật chính user đang login. Nếu id path khác với id của userDetails -> 403.
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Lấy user thực hiện request
        User current = userService.findByEmail(userDetails.getUsername())
                                  .orElseThrow();

        // Chỉ cho phép update chính mình
        if (!current.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Bạn không có quyền cập nhật user này");
        }

        // Thực hiện update
        UserProfileDTO updated = userService.updateUser(id, dto);
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
