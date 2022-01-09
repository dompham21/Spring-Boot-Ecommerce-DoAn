package com.luv2code.doan.controller;


import com.luv2code.doan.entity.User;
import com.luv2code.doan.service.RoleService;
import com.luv2code.doan.service.UserService;
import com.luv2code.doan.utils.GetSiteUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        return "auth/signup";
    }

    @PostMapping("/signup")
    public String saveUser(@Valid User user, BindingResult errors, RedirectAttributes redirectAttributes, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {

        if(userService.getUserByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "user", "Email đã được sử dụng!");
        }
        if(errors.hasErrors())
            return "auth/signup";
        else {
            user.addRole(roleService.getRoleByID(1));

            userService.saveUser(user);
            String siteUrl = GetSiteUrl.getSiteUrl(request);
            userService.sendVerificationEmail(user, siteUrl);

            redirectAttributes.addFlashAttribute("email",user.getEmail());

            return "redirect:/signup/success";
        }

    }

    @GetMapping("/signup/success")
    public String signupSuccess() {
        return "auth/signup_success";
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (userService.verify(code)) {
            return "auth/verify_success";
        } else {
            return "auth/verify_fail";
        }
    }
}
