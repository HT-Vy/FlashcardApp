// package com.example.demo.security;

// import java.util.ArrayList;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.example.demo.model.User;
// import com.example.demo.repository.UserRepository;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {

//     @Autowired
//     private UserRepository userRepository;

//     // Spring Security sẽ gọi hàm này khi cần lấy thông tin user từ email
//     @Override
//     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//         User user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

//         return new org.springframework.security.core.userdetails.User(
//                 user.getEmail(),                   // username
//                 user.getPassword(),               // password (đã mã hóa)
//                 new ArrayList<>()                 // authorities/roles (có thể để trống nếu chưa cần)
//         );
//     }
// }
