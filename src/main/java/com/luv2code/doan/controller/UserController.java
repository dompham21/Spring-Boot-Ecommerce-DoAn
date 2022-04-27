package com.luv2code.doan.controller;


import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.StorageUploadFileException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.service.RoleService;
import com.luv2code.doan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin/user")
    public String adminFirstPageUser() {
        return "redirect:/admin/user/page/1";
    }

    @GetMapping("/admin/user/page/{pageNum}")
    public String adminPageUser(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "id") String sortField,
                             @RequestParam(required = false) String sortDir) {

        model.addAttribute("sortField", sortField);

        if(sortDir == null) sortDir = "asc";
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);


        Page<User> page = userService.listByPage(pageNum, keyword, sortField, sortDir);
        List<User> listUsers = page.getContent();
        long startCount = (pageNum - 1) * userService.USER_PER_PAGE + 1;
        long endCount = startCount +  userService.USER_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }



        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listUsers", listUsers);


        return "user/users";
    }

    @GetMapping("/admin/user/add")
    public String addUser(Model model) {
        User user = new User();
        user.addRole(roleService.getRoleByID(1));
        user.setIsActive(true);
        List<Role> listRoles = userService.listRoles();

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", user);
        }
        model.addAttribute("listRoles", listRoles);

        return "user/new_user";
    }

    @PostMapping("/admin/user/add")
    public String saveUser(User user, BindingResult errors, RedirectAttributes redirectAttributes) {

        if (user.getLastName().matches(".*\\d+.*")) {
            errors.rejectValue("lastName", "user", "Họ không được chứa số!");
        }
        if (user.getLastName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*") ) {
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
        if(errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", errors);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/admin/user/add";
        }
        else {
            String encodePassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
            user.setRegistrationDate(new Date());
            userService.saveUserAdmin(user);
            redirectAttributes.addFlashAttribute("messageSuccess", "The user has been saved successfully.");
            return "redirect:/admin/user/page/1";
        }
    }

    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("messageSuccess", "The user ID " + id + " has been deleted successfully");
        }
        catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }
        return "redirect:/admin/user/page/1";
    }


    @GetMapping("/admin/user/edit/{id}")
    public String editUser(@PathVariable("id") Integer id,RedirectAttributes redirectAttributes, Model model) {
        try {
            List<Role> listRoles = userService.listRoles();
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("isEdit", true);


            if (!model.containsAttribute("user")) {
                User user = userService.getUserByID(id);
                model.addAttribute("user", user);
            }
            return "user/new_user";
        }
        catch ( UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/user/page/1";

        }
    }

    @PostMapping("/admin/user/edit/{id}")
    public String saveEditUser(User user, BindingResult errors, RedirectAttributes redirectAttributes,
                                  @PathVariable("id") Integer id) {

        try {
            User existUser = userService.getUserByID(id);
            if ((user.getLastName().matches(".*\\d+.*"))) {
                errors.rejectValue("lastName", "user", "Họ không được chứa số!");
            }
            if (user.getLastName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*") ) {
                errors.rejectValue("lastName", "user", "Họ không được chứa ký tự đặc biệt!");
            }
            if (user.getFirstName().matches(".*\\d+.*") ) {
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
            User userCheckEmailUnique = userService.getUserByEmail(user.getEmail());
            if(userCheckEmailUnique != null && !userCheckEmailUnique.getId().equals(existUser.getId())) {
                errors.rejectValue("email", "user", "Email đã được sử dụng!");
            }
            if (!user.getPhone().matches("\\d{10,}")) {
                errors.rejectValue("phone", "user", "Số điện thoại không hợp lệ!");
            }
            User userCheckPhoneUnique = userService.getUserByPhone(user.getPhone());
            if (userCheckPhoneUnique != null && !userCheckPhoneUnique.getId().equals(existUser.getId())) {
                errors.rejectValue("phone", "user", "Số điện thoại đã được sử dụng!");
            }


            if (errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", errors);
                redirectAttributes.addFlashAttribute("user", user);
                return "redirect:/admin/user/edit/" + id;
            } else {
                user.setPassword(existUser.getPassword());
                user.setRegistrationDate(existUser.getRegistrationDate());

                userService.saveUserAdmin(user);

                redirectAttributes.addFlashAttribute("messageSuccess", "The user has been edited successfully.");
                return "redirect:/admin/user/page/1";


            }
        } catch ( UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/user/page/1";
        }
    }
}
