package com.owl.owlserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableGlobalMethodSecurity(prePostEnabled = true)

@Configuration
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
            "/actuator/info",
            "/actuator/health"
    };


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/posEndpoint").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers("/hoEndpoint").access("hasRole('USER')")
                .anyRequest().authenticated()
                .and()
                .httpBasic();

        http.headers().frameOptions().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.inMemoryAuthentication()
                .withUser("warehouse")
                .password("{noop}warehouse")
                .roles("WAREHOUSE");
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}admin")
                .roles("ADMIN");
        auth.inMemoryAuthentication()
                .withUser("office")
                .password("{noop}office")
                .roles("OFFICE");
        auth.inMemoryAuthentication()
                .withUser("store")
                .password("{noop}store")
                .roles("store");
    }
}