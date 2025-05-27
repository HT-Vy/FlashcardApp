// package com.htv.flashcard.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
//     private final JwtFilter jwtFilter;
//     private final CustomUserDetailsService customUserDetailsService;

//     public SecurityConfig(JwtFilter jwtFilter,
//                           CustomUserDetailsService customUserDetailsService) {
//         this.jwtFilter = jwtFilter;
//         this.customUserDetailsService = customUserDetailsService;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     /**
//      * Tạo AuthenticationManager thủ công, bind đúng UserDetailsService + PasswordEncoder
//      */
//      @Bean
//   public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//     AuthenticationManagerBuilder authBuilder =
//       http.getSharedObject(AuthenticationManagerBuilder.class);

//     authBuilder
//       .userDetailsService(customUserDetailsService)
//       .passwordEncoder(passwordEncoder());

//     return authBuilder.build();
//   }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable())
//             .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(auth -> auth
//                 // static assets
//                 .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                 // cho phép register & login
//                 .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
//                 // admin-only
//                 .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                 // những API khác phải xác thực
//                 .requestMatchers("/api/**").authenticated()
//                 // còn lại (favicon, html page tĩnh ...) cho phép
//                 .anyRequest().permitAll()
//             )
//             .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }
// }
package com.htv.flashcard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtFilter jwtFilter,
                          CustomUserDetailsService customUserDetailsService) {
        this.jwtFilter = jwtFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Tạo AuthenticationManager thủ công, bind đúng UserDetailsService + PasswordEncoder
     */
     @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authBuilder =
      http.getSharedObject(AuthenticationManagerBuilder.class);

    authBuilder
      .userDetailsService(customUserDetailsService)
      .passwordEncoder(passwordEncoder());

    return authBuilder.build();
  }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // static assets
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // cho phép register & login
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                // admin-only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // những API khác phải xác thực
                .requestMatchers("/api/**").authenticated()
                // còn lại (favicon, html page tĩnh ...) cho phép
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}