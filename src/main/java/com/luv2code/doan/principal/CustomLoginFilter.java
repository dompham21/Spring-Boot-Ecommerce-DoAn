package com.luv2code.doan.principal;

import com.luv2code.doan.bean.RecaptchaResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    String recatchaUrl = "https://www.google.com/recaptcha/api/siteverify";
    String secret = "6LeauFIfAAAAAAqwjGqe7ZOh3m6VeZb0MvvlNjmE";
    public CustomLoginFilter(String loginUrl, String httpMethod) {
        setUsernameParameter("email");
        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginUrl, httpMethod));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String recaptchaFormResponse = request.getParameter("g-recaptcha-response");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", secret);
        map.add("response", recaptchaFormResponse);
        HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        RecaptchaResponse gResponse = restTemplate.postForObject(recatchaUrl, httpRequest, RecaptchaResponse.class);

        System.out.println("Recaptcha response: " + gResponse);
        if(gResponse.getErrorCodes() != null) {
            try {
                response.sendRedirect("/login?errorRcaptcha");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        System.out.println("Before processing authentication....");
        return super.attemptAuthentication(request, response);
    }


}
