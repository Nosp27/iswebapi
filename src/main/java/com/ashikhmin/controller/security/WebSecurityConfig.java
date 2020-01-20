package com.ashikhmin.controller.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        getHttp().csrf().disable();
        http
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/ping", "/actor/login_fail").permitAll()
                .anyRequest().authenticated()
                .and().oauth2Login();
    }
}
