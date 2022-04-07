package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Review;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.service.RoleService;
import com.luv2code.doan.service.UserService;
import com.luv2code.doan.utils.GetSiteUrl;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

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
    public String saveUser(User user, BindingResult errors, RedirectAttributes redirectAttributes, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        if (user.getLastName().matches(".*\\d+.*")) {
            errors.rejectValue("lastName", "user", "Họ không được chứa số!");
        }
        if (user.getLastName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
            errors.rejectValue("lastName", "user", "Họ không được chứa ký tự đặc biệt!");
        }
        if (user.getFirstName().matches(".*\\d+.*")) {
            errors.rejectValue("firstName", "user", "Tên không được chứa số!");
        }
        if (user.getFirstName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
            errors.rejectValue("firstName", "user", "Tên không được chứa ký tự đặc biệt!");
        }
        if (user.getLastName().length() > 100) {
            errors.rejectValue("lastName", "user", "Họ không được dài quá 100 ký tự!");
        }
        if (user.getFirstName().length() > 50) {
            errors.rejectValue("firstName", "user", "Tên không được dài quá 100 ký tự!");
        }
        if (user.getEmail().length() > 100) {
            errors.rejectValue("email", "user", "Email không được dài quá 100 ký tự!");
        }
        if (userService.getUserByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "user", "Email đã được sử dụng!");
        }
        if (!user.getPhone().matches("\\d{10,}")) {
            errors.rejectValue("phone", "user", "Số điện thoại không hợp lệ!");
        }
        if (userService.getUserByPhone(user.getPhone()) != null) {
            errors.rejectValue("phone", "user", "Số điện thoại đã được sử dụng!");
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

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }

        return "auth/forgot_password";
    }

    @PostMapping("/forgot-password")
    public String postForgotPassword(User user, BindingResult errors, RedirectAttributes redirectAttributes, Model model) throws UnsupportedEncodingException, MessagingException {
        if (user.getEmail() == null) {
            errors.rejectValue("email", "user", "Email không được bo trong!");
        }
        if (user.getEmail().length() > 100) {
            errors.rejectValue("email", "user", "Email không được dài quá 100 ký tự!");
        }

        if(errors.hasErrors()) {
            return "auth/forgot_password";
        }
        else {
            String email = user.getEmail();
            String token =  RandomString.make(15);


            User customer = userService.getUserByEmail(email);
            if(customer != null) {

                String encodePassword = passwordEncoder.encode(token);
                userService.sendEmailPassword(token, customer);
                customer.setPassword(encodePassword);
                userService.saveEditUser(customer);
                model.addAttribute("email", customer.getEmail());
                return "auth/newpass-success";
            }
            else {
                redirectAttributes.addFlashAttribute("messageError", "Tài khoản không tồn tại, vui lòng nhập email đã đăng ký tài khoản!");
                return "redirect:/forgot-password";
            }

        }
    }
}
