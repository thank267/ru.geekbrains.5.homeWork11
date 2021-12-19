package ru.geekbrains.homework11.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.geekbrains.homework11.services.UserService;

@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST,"/products/v1/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.DELETE,"/products/v1/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.PUT,"/products/v1/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.GET,"/products/v1/**").permitAll()
                .antMatchers("/products/v1/**").denyAll()
                .antMatchers(HttpMethod.GET,"/users/v1/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/users/v1/**").hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.PUT,"/users/v1/updateRole/**").hasRole("SUPER_ADMIN")
                .antMatchers("/users/v1/**").denyAll()
                .and()
                .httpBasic()
                .and()
                .logout()
                .permitAll();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }
}
