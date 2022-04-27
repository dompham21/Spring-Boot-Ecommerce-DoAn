package com.luv2code.doan.interceptor;


import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.CartService;
import com.luv2code.doan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@Component
public class AdminIntercaptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
              Integer userId = ((UserPrincipal) principal).getId();
        }
        else {
            System.out.println("redirect to login");

            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

}
