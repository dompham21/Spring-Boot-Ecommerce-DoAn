package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/user")
    public String adminFirstPageUser() {
        return "redirect:/admin/user/page/1";
    }

    @GetMapping("/admin/user/page/{pageNum}")
    public String adminPageUser(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String sortField,
                             @RequestParam(required = false) String sortDir) {

        if(sortField != null) {
            model.addAttribute("sortField", sortField);
        }
        if(sortDir == null) sortDir = "asc";
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        if(keyword != null) {
            model.addAttribute("keyword", keyword);
        }

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
}
