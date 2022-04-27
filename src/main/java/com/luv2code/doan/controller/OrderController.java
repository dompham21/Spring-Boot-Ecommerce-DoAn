package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Order;
import com.luv2code.doan.entity.OrderStatus;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.OrderNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.repository.OrderStatusRepository;
import com.luv2code.doan.service.CartService;
import com.luv2code.doan.service.OrderService;
import com.luv2code.doan.service.OrderStatusService;
import com.luv2code.doan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderStatusService orderStatusService;

    @GetMapping("/profile/order/info")
    public String listOrderFirstPage(@AuthenticationPrincipal UserPrincipal loggedUser, Model model, RedirectAttributes redirectAttributes) {
        return profileOrderInfo(loggedUser, model, 1,null, null, null, null, redirectAttributes);
    }

    @GetMapping("/profile/order/info/page/{pageNum}")
    public String profileOrderInfo(@AuthenticationPrincipal UserPrincipal loggedUser, Model model,
                                   @PathVariable(name = "pageNum") Integer pageNum,
                                   @RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
                                   @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
                                   @RequestParam(name = "status", required = false) String status,
                                   RedirectAttributes redirectAttributes) {
        Integer id = loggedUser.getId();
        try {
            log.info("Start profile order!!!!");
            User user = userService.getUserByID(id);
            log.info(user.toString());
            List<Cart> listCarts = cartService.findCartByUser(loggedUser.getId());
            double estimatedTotal = 0;

            for (Cart item : listCarts) {
                estimatedTotal += item.getSubtotal();

            }

            if(status == null) {
                status = "ALL";
            }


            Page<Order> page = orderService.listForUserByPage(user, pageNum, keyword, startDate, endDate, status);
            List<Order> listOrders = page.getContent();


            long startCount = (long) (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
            long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
            if (endCount > page.getTotalElements()) {
                endCount = page.getTotalElements();
            }
            List<OrderStatus> orderStatusList = orderStatusService.listOrderStatus();

            log.info("user has add to model");
            model.addAttribute("listCarts", listCarts);
            model.addAttribute("estimatedTotal", estimatedTotal);
            model.addAttribute("orderStatusList", orderStatusList);
            model.addAttribute("user", user);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("listOrders", listOrders);
            model.addAttribute("keyword", keyword);
            model.addAttribute("status", status);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "profile-user/order-info";
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/";
        }

    }

    @GetMapping("/profile/order/info/detail/{id}")
    public String detailProfileUser(@AuthenticationPrincipal UserPrincipal loggedUser, Model model,
                                    @PathVariable(name = "id") Integer orderId,
                                    RedirectAttributes redirectAttributes) {

        Integer id = loggedUser.getId();
        try {
            log.info("Start detail order!!!!");
            User user = userService.getUserByID(id);
            Order order = orderService.getOrder(orderId, user);
            model.addAttribute("order", order);
            return "profile-user/order-details-modal";
        }
        catch (UserNotFoundException | OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/";
        }
    }



    @GetMapping("/admin/order")
    public String listOrderAdminFirstPage(@AuthenticationPrincipal UserPrincipal loggedUser, Model model, RedirectAttributes redirectAttributes) {

        return listOrderAdminPage(loggedUser, model, 1,null, null, null, null, redirectAttributes);
    }

    @GetMapping("/admin/order/page/{pageNum}")
    public String listOrderAdminPage(@AuthenticationPrincipal UserPrincipal loggedUser, Model model,
                                     @PathVariable(name = "pageNum") Integer pageNum,
                                     @RequestParam(name = "keyword", required = false) String keyword,
                                     @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
                                     @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
                                     @RequestParam(name = "status", required = false) String status,
                                     RedirectAttributes redirectAttributes) {
        Integer id = loggedUser.getId();
        try {
            User user = userService.getUserByID(id);


            if(status == null) {
                status = "ALL";
            }


            Page<Order> page = orderService.listByPage(pageNum, keyword, startDate, endDate, status);
            List<Order> listOrders = page.getContent();


            long startCount = (long) (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
            long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
            if (endCount > page.getTotalElements()) {
                endCount = page.getTotalElements();
            }
            List<OrderStatus> orderStatusList = orderStatusService.listOrderStatus();

            model.addAttribute("orderStatusList", orderStatusList);
            model.addAttribute("user", user);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("listOrders", listOrders);
            model.addAttribute("keyword", keyword);
            model.addAttribute("status", status);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "order/orders";
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin";
        }
    }

    @GetMapping("/admin/order/accept")
    public String acceptOrder(@RequestParam("id") Integer id, @RequestParam("statusId") Integer statusId) throws OrderNotFoundException {

        orderService.acceptOrder(id, statusId);
        return "redirect:/admin/order";
    }

    @GetMapping("/admin/order/deny")
    public String denyOrder(@RequestParam("id") Integer id, @RequestParam("statusId") Integer statusId) throws OrderNotFoundException {

        orderService.denyOrder(id, statusId);
        return "redirect:/admin/order";
    }

    @GetMapping("/profile/order/requestCancel")
    public String requestCancel(@RequestParam("id") Integer id) throws OrderNotFoundException {
        orderService.requestCancel(id);
        return "redirect:/profile/order/info";
    }

    @GetMapping("/profile/order/cancelRequest")
    public String cancelRequest(@RequestParam("id") Integer id) throws OrderNotFoundException {
        orderService.cancelRequest(id);
        return "redirect:/profile/order/info";
    }


}
