package com.luv2code.doan.controller;

import com.luv2code.doan.bean.Items;
import com.luv2code.doan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private ReportService reportService;


    @GetMapping("/admin")
    public String adminDashboard(Model model) throws ParseException {
        List<Items> listEarn = reportService.reportEarn();
        List<Items> listUser = reportService.reportUser();
        List<Object> listOrder = reportService.reportOrder();


        List<String> listEarnKey = new ArrayList<>();
        List<Long> listEarnValue =  new ArrayList<>();
        List<String> listOrderKey = new ArrayList<>();
        List<Long> listOrderValue =  new ArrayList<>();
        List<Long> listUserValue =  new ArrayList<>();


        long countUserByWeek = reportService.countUserByWeek();
        long countOrderByWeek = reportService.countOrderByWeek();
        long countReviewByWeek = reportService.countReviewByWeek();
        long totalOrderByWeek = reportService.getTotalOrderByWeek();


        for(Items item : listEarn) {
            listEarnKey.add(item.getName());
            listEarnValue.add(item.getValue());
        }

        for(Items item: listUser) {
            listUserValue.add(item.getValue());
        }


        model.addAttribute("listUserValue", listUserValue);
        model.addAttribute("listEarnKey", listEarnKey);
        model.addAttribute("listEarnValue", listEarnValue);
        model.addAttribute("listOrderKey", listOrderKey);
        model.addAttribute("listOrderValue", listOrderValue);
        model.addAttribute("listOrder", listOrder);

        model.addAttribute("countUserByWeek", countUserByWeek);
        model.addAttribute("countOrderByWeek", countOrderByWeek);
        model.addAttribute("countReviewByWeek", countReviewByWeek);
        model.addAttribute("totalOrderByWeek", totalOrderByWeek);

        return "admin";
    }



}
