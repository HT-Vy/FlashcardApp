package com.htv.flashcard.service;

import java.util.Collections;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.htv.flashcard.DTO.UserDTO;
import com.htv.flashcard.DTO.UserProfileDTO;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Cập nhật fullName và avatarUrl (nếu có)
     */
    public UserProfileDTO updateUserProfile(Long id, String fullName, MultipartFile avatar) {
        User user = userRepository.findById(id)
                      .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 1) Cập nhật fullName
        user.setFullName(fullName);

        // 2) Nếu có file avatar, lưu file và cập nhật avatarUrl
        if (avatar != null && !avatar.isEmpty()) {
            // Ví dụ lưu trong thư mục /uploads/ với tên file duy nhất
            String filename = UUID.randomUUID() + "-" + avatar.getOriginalFilename();
            Path target = Paths.get("src/main/resources/static/uploads").resolve(filename);
            try {
                Files.createDirectories(target.getParent());
                avatar.transferTo(target);
                user.setAvatarUrl("/uploads/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi lưu avatar", e);
            }
        }

        // 3) Lưu lại và map sang DTO
        User saved = userRepository.save(user);
        UserProfileDTO dto = new UserProfileDTO();
        dto.setFullName(saved.getFullName());
        dto.setEmail(saved.getEmail());
        dto.setAvatarUrl(saved.getAvatarUrl());
        // nếu muốn trả sets:
        // dto.setFlashcardSets(
        //     saved.getFlashcardSets().stream()
        //          .map(fs -> /* map sang FlashcardSetDTO */ )
        //          .collect(Collectors.toList())
        // );
        return dto;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                     .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
        // Nếu User của bạn chưa implement UserDetails, bạn có thể return:
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(/* giả sử bạn chưa phân quyền, truyền rỗng */ Collections.emptyList())
            .build();
    }
    // Trả về user dựa vào id
    public User getUserById(Long userId){
        return userRepository.findById(userId).orElseThrow();
    }
}

