package com.luv2code.doan.controller;


import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.AddressNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.AddressService;
import com.luv2code.doan.service.CartService;
import com.luv2code.doan.service.OrderService;
import com.luv2code.doan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CheckoutController {
    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);


    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;



    @GetMapping("/checkout")
    public String checkout(Model model, @AuthenticationPrincipal UserPrincipal loggedUser) {
        if(loggedUser != null) {
            List<Cart> listCarts = cartService.findCartByUser(loggedUser.getId());
            List<Province> listProvinces = addressService.getListProvinces();
            List<Address> listAddresses = addressService.getListAddressByUserId(loggedUser.getId());
            List<District> listDistricts = addressService.getListDistrict();
            List<Ward> listWards = addressService.getListWard();


            double estimatedTotal = 0;

            for (Cart item : listCarts) {
                estimatedTotal += item.getSubtotal();

            }
            log.info(estimatedTotal + "");
            model.addAttribute("listDistricts", listDistricts);
            model.addAttribute("listWards", listWards);
            model.addAttribute("listCarts", listCarts);
            model.addAttribute("estimatedTotal", estimatedTotal);
            model.addAttribute("listProvinces", listProvinces);
            model.addAttribute("listAddresses", listAddresses);
            if (!model.containsAttribute("address")) {
                model.addAttribute("address", new Address());
            }
        }
        return "checkout/place-order";
    }


    @PostMapping("/checkout")
    public String placeOrder(Address address, BindingResult errors, @AuthenticationPrincipal UserPrincipal loggedUser,
                             RedirectAttributes redirectAttributes,
                             @RequestParam("radio-tab") String radioTab,
                             @RequestParam(name = "radio-address", required = false)  Integer existAddressId, Model model) {
        Integer id = loggedUser.getId();
        try {
            User user = userService.getUserByID(id);
            Address getAddress = null;
            if(radioTab.equals("old-address")) {
                getAddress = addressService.getAddress(existAddressId, id);
            }
            else if(radioTab.equals("new-address")) {
                if (address.getLastName().matches(".*\\d+.*")) {
                    errors.rejectValue("lastName", "address", "Họ không được chứa số!");
                }
                if (address.getLastName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
                    errors.rejectValue("lastName", "address", "Họ không được chứa ký tự đặc biệt!");
                }
                if (address.getFirstName().matches(".*\\d+.*")) {
                    errors.rejectValue("firstName", "address", "Tên không được chứa số!");
                }
                if (address.getFirstName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
                    errors.rejectValue("firstName", "address", "Tên không được chứa ký tự đặc biệt!");
                }
                if (address.getLastName().length() > 100) {
                    errors.rejectValue("lastName", "address", "Họ không được dài quá 100 ký tự!");
                }
                if (address.getFirstName().length() > 50) {
                    errors.rejectValue("firstName", "address", "Tên không được dài quá 100 ký tự!");
                }
                if (address.getEmail().length() > 100) {
                    errors.rejectValue("email", "address", "Email không được dài quá 100 ký tự!");
                }
                if (!address.getPhone().matches("\\d{10,}")) {
                    errors.rejectValue("phone", "address", "Số điện thoại không hợp lệ!");
                }
                if(address.getSpecificAddress().length() > 200) {
                    errors.rejectValue("lastName", "address", "Địa chỉ cụ thể không được dài quá 200 ký tự!");
                }
                if(errors.hasErrors()) {
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.address", errors);
                    redirectAttributes.addFlashAttribute("address", address);
                    return "redirect:/checkout";
                }
                else {
                    address.setUser(user);

                    long numberAddressExist = addressService.getCountAddressByUserId(id);

                    // If user doesn't have any address then set default as this address
                    if(numberAddressExist == 0) {
                        address.setDefault(true);
                    }
                    getAddress = addressService.save(address);
                }
            }

            List<Cart> cartList = cartService.findCartByUser(id);
            Order order = orderService.createOrder(user, getAddress, cartList);
            cartService.deleteCartItemByUser(id);

            return "checkout/order-completed";

        }
        catch (UserNotFoundException | AddressNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/checkout";
        }


    }



}
