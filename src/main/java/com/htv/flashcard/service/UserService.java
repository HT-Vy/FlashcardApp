package com.htv.flashcard.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
     * Cập nhật user và trả về UserProfileDTO
     */
    public UserProfileDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                      .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        // Chỉ cho phép đổi fullName, không đổi email/password tại đây
        user.setFullName(dto.getFullName());
        User saved = userRepository.save(user);

        // Map User → UserProfileDTO
        UserProfileDTO profile = new UserProfileDTO();
        profile.setFullName(saved.getFullName());
        profile.setEmail(saved.getEmail());
        // Nếu muốn trả cả flashcardSets, bạn có thể map thêm:
        // profile.setFlashcardSets(...);

        return profile;
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

