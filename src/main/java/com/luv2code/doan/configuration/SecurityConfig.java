package com.luv2code.doan.configuration;


import com.luv2code.doan.principal.CustomLoginFilter;
import com.luv2code.doan.principal.CustomUserDetailService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .and().csrf().disable().authorizeRequests()
            .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers( "/profile/**", "/checkout/**", "/cart/**").hasAnyAuthority("ROLE_USER")
            .antMatchers("/api/**","/webjars/**", "/images/**",
                    "/signup/**", "/verify/**","/auth/**",
                    "/login/**", "/logout/**","/assets/**", "/css/**","/product/**","/brand/**","/","/js/**"
                    ,"/search/**", "/forgot-password/**").permitAll()
            .anyRequest().authenticated()
            .and()
//                .addFilterBefore(getCustomLoginFilter(), CustomLoginFilter.class)
            .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
                .defaultSuccessUrl("/")
            .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .permitAll()
            .and()
                .rememberMe()
                .rememberMeParameter("remember-me")
                .key("AbcDefgKLDSLmvop_0123456789")
                .tokenValiditySeconds( 7 * 24 * 60 * 60); // 7days


    }


    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("static/**");
    }

    private CustomLoginFilter getCustomLoginFilter() throws Exception {
        CustomLoginFilter filter = new CustomLoginFilter("/login", "POST");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
                httpServletResponse.sendRedirect("login?error");
            }
        });
        return filter;
    }

}
