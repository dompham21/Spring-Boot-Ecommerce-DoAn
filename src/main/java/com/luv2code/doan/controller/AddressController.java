package com.luv2code.doan.controller;


import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.AddressNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.AddressService;
import com.luv2code.doan.service.CartService;
import com.luv2code.doan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class AddressController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping("/profile/address")
    public String getListAddress(@AuthenticationPrincipal UserPrincipal loggedUser, Model model,
                                 RedirectAttributes redirectAttributes) {
        Integer id = loggedUser.getId();
        try {
            List<Cart> listCarts = cartService.findCartByUser(id);
            double estimatedTotal = 0;

            for (Cart item : listCarts) {
                estimatedTotal += item.getSubtotal();

            }
            model.addAttribute("listCarts", listCarts);
            model.addAttribute("estimatedTotal", estimatedTotal);

            User user = userService.getUserByID(id);
            List<Address> listAddresses = addressService.getListAddressByUserId(id);

            model.addAttribute("listAddresses", listAddresses);
            model.addAttribute("user", user);
            return "profile-user/address";
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "profile-user/address";
        }
    }

    @GetMapping("/profile/address/add")
    public String newAddress(@AuthenticationPrincipal UserPrincipal loggedUser, Model model,
                                 RedirectAttributes redirectAttributes) {
        Integer id = loggedUser.getId();
        try {
            User user = userService.getUserByID(id);
            List<Cart> listCarts = cartService.findCartByUser(id);
            double estimatedTotal = 0;

            for (Cart item : listCarts) {
                estimatedTotal += item.getSubtotal();

            }
            model.addAttribute("listCarts", listCarts);
            model.addAttribute("estimatedTotal", estimatedTotal);
            List<Province> listProvinces = addressService.getListProvinces();
            List<District> listDistricts = addressService.getListDistrict();
            List<Ward> listWards = addressService.getListWard();

            Address address = new Address();


            model.addAttribute("listDistricts", listDistricts);
            model.addAttribute("listWards", listWards);
            model.addAttribute("listProvinces", listProvinces);
            model.addAttribute("address", address);
            model.addAttribute("user", user);
            model.addAttribute("isEdit", false);

            return "profile-user/edit-address";
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "profile-user/edit-address";
        }
    }

    @PostMapping("/profile/address/add")
    public String saveAddress(@Valid Address address, BindingResult errors, @AuthenticationPrincipal UserPrincipal loggedUser,
                              RedirectAttributes redirectAttributes, Model model) {
        Integer id = loggedUser.getId();

        try {
            User user = userService.getUserByID(id);
            if(errors.hasErrors()) {
                List<Province> listProvinces = addressService.getListProvinces();

                Address addressNew = new Address();

                model.addAttribute("listProvinces", listProvinces);
                model.addAttribute("address", addressNew);
                model.addAttribute("user", user);
                return "profile-user/edit-address";
            }
            else {
                address.setUser(user);
                LOGGER.info(address.toString());

                long numberAddressExist = addressService.getCountAddressByUserId(id);

                // If user doesn't have any address then set default as this address
                if(numberAddressExist == 0) {
                    address.setDefault(true);
                }
                addressService.save(address);
                redirectAttributes.addFlashAttribute("messageSuccess", "The address has been saved successfully.");
                return "redirect:/profile/address";
            }
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/profile/address";
        }
    }
    @GetMapping("/profile/address/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer addressId,
                                    @AuthenticationPrincipal UserPrincipal loggedUser,RedirectAttributes redirectAttributes)  {

        Integer id = loggedUser.getId();

        try {
            User user = userService.getUserByID(id);
            addressService.setDefaultAddress(addressId, id);
            redirectAttributes.addFlashAttribute("messageSuccess", "The address has been set as default.");
            return "redirect:/profile/address";
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/profile/address";
        }
    }

    @GetMapping("/profile/address/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId,
                                @AuthenticationPrincipal UserPrincipal loggedUser, RedirectAttributes redirectAttributes) {
        Integer id = loggedUser.getId();

        try {
            User user = userService.getUserByID(id);
            addressService.delete(addressId, id);
            redirectAttributes.addFlashAttribute("messageSuccess", "The address has been deleted.");
            return "redirect:/profile/address";
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/profile/address";
        }
    }

    @GetMapping("/profile/address/edit/{id}")
    public String editAddress(@PathVariable("id") Integer addressId,
                                @AuthenticationPrincipal UserPrincipal loggedUser, RedirectAttributes redirectAttributes, Model model) {
        Integer id = loggedUser.getId();
        try {
            User user = userService.getUserByID(id);
            List<Province> listProvinces = addressService.getListProvinces();
            List<District> listDistricts = addressService.getListDistrict();
            List<Ward> listWards = addressService.getListWard();
            Address address = addressService.getAddress(addressId, id);
            List<Cart> listCarts = cartService.findCartByUser(id);

            double estimatedTotal = 0;

            for (Cart item : listCarts) {
                estimatedTotal += item.getSubtotal();

            }
            model.addAttribute("listCarts", listCarts);
            model.addAttribute("estimatedTotal", estimatedTotal);


            model.addAttribute("listDistricts", listDistricts);
            model.addAttribute("listWards", listWards);
            model.addAttribute("listProvinces", listProvinces);
            model.addAttribute("address", address);
            model.addAttribute("user", user);
            model.addAttribute("isEdit", true);
            return "profile-user/edit-address";
        }
        catch (UserNotFoundException | AddressNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "profile-user/edit-address";
        }
    }

    @PostMapping("/profile/address/edit/{id}")
    public String saveEditAddress(@Valid Address address, BindingResult errors, @AuthenticationPrincipal UserPrincipal loggedUser,
                                  RedirectAttributes redirectAttributes, Model model, @PathVariable("id") Integer addressId) {
        Integer id = loggedUser.getId();

        try {
            User user = userService.getUserByID(id);
            Address addressExist = addressService.getAddress(addressId, id);

            if(errors.hasErrors()) {
                List<Province> listProvinces = addressService.getListProvinces();

                model.addAttribute("listProvinces", listProvinces);
                model.addAttribute("address", addressExist);
                model.addAttribute("user", user);
                return "profile-user/edit-address";
            }
            else {
                address.setUser(addressExist.getUser());
                address.setDefault(addressExist.isDefault());
                addressService.save(address);
                redirectAttributes.addFlashAttribute("messageSuccess", "The address has been saved successfully.");
                return "redirect:/profile/address";
            }
        }
        catch (UserNotFoundException | AddressNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/profile/address";
        }
    }


}
