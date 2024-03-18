package Remoa.BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecureConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder().encode("admin1234"))
                .roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").hasRole("ADMIN") // 다른 요청에는 인증이 필요
                .and()
                .formLogin() // 로그인 페이지 설정
                .permitAll() // 로그인 페이지에는 모든 사용자가 접근할 수 있어야 함
                .and()
                .logout()
                .permitAll() // 로그아웃 페이지에는 모든 사용자가 접근할 수 있어야 함
                .and()
                .sessionManagement()
                .maximumSessions(-1) // 세션 제한 없음
                .maxSessionsPreventsLogin(false) // 중복 접속시 마지막 세션만 유지
                .and();
        http.csrf().disable(); // CSRF 보안 비활성화
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}