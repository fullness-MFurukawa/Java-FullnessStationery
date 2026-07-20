package jp.co.fullness.ec.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Spring Security の設定。
 * 認証処理はコントローラ(LoginController)で自前実装するため formLogin は使わず、
 * 未認証アクセス時のログイン画面誘導・認可・ログアウト・PasswordEncoder を構成する。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 自前認証で使用する AuthenticationManager を公開する。 */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .anyRequest().hasRole("ADMIN"))
            // 未認証でのアクセスはログイン画面へ誘導
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/admin/login")))
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login")
                .permitAll())
            // セッション固定攻撃対策
            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.changeSessionId()));

        return http.build();
    }
}
