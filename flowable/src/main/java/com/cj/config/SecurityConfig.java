package com.cj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin().loginProcessingUrl("/doLogin")
                .and()
                // 禁用掉csrf攻击防御机制
                .csrf().disable();
    }

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//            http.authorizeRequests().anyRequest().authenticated()
//                    .and()
//                    .formLogin()
//                    .and()
//                    // 禁用掉csrf攻击防御机制
//                    .csrf().disable();
//
//            return http.build();
//
//    }
}
