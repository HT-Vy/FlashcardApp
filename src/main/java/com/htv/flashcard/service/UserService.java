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

    public User updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElseThrow();
        user.setFullName(dto.getFullName());
        // không cập nhật email ở đây để giữ duy nhất
        return userRepository.save(user);
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
}

