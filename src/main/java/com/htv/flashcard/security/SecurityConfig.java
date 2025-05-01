// package com.htv.flashcard.security;


// import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final JwtFilter jwtFilter;

//     public SecurityConfig(JwtFilter jwtFilter) {
//         this.jwtFilter = jwtFilter;
//     }

//     /**
//      * Bean AuthenticationManager từ AuthenticationConfiguration
//      */
//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//         return authConfig.getAuthenticationManager();
//     }

//     /**
//      * Bean PasswordEncoder sử dụng BCrypt
//      */
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     /**
//      * Cấu hình luồng bảo mật sử dụng Lambda DSL mới (Spring Security 6)
//      */
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//          http
//             // Tắt CSRF vì đây là REST API
//             .csrf(csrf -> csrf.disable())

//             // Cho phép ai cũng có thể gọi register và login
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
//                 // Cho phép truy cập tài nguyên static (CSS, JS, images)
//                 // Cho phép truy cập các file tĩnh: css, js, hình ảnh...
//                 .requestMatchers(
//                     "/assets/**", "/css/**", "/js/**", "/images/**", 
//                     "/pages/**", "/favicon.ico", "/**/*.html", "/**/*.js", "/**/*.css"
//                 ).permitAll()
//                 // Các request khác bắt buộc phải có token
//                 .anyRequest().authenticated()
//             )

//             // Stateless: không tạo session
//             .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//             // Chèn filter kiểm token trước filter mặc định của Spring
//             .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }
// }

package com.htv.flashcard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Tạo bean AuthenticationManager để inject vào AuthController
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean PasswordEncoder sử dụng BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình SecurityFilterChain cho Spring Security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF vì dùng JWT, không dùng session
            .csrf(csrf -> csrf.disable())

            // Không dùng session, giữ trạng thái stateless
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Cấu hình đường dẫn và quyền
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập tài nguyên static (CSS, JS, images)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // Cho phép mọi người đăng ký và đăng nhập
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                // Các API khác đều yêu cầu phải xác thực qua JWT
                .requestMatchers("/api/**").authenticated()
                // SVG, favicon
                .anyRequest().permitAll()
            )

            // Thêm filter để xử lý JWT trước UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
