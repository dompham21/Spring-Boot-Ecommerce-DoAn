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



            model.addAttribute("listDistricts", listDistricts);
            model.addAttribute("listWards", listWards);
            model.addAttribute("listProvinces", listProvinces);
            model.addAttribute("user", user);
            model.addAttribute("isEdit", false);

            if (!model.containsAttribute("address")) {
                Address address = new Address();
                model.addAttribute("address", address);
            }

            return "profile-user/edit-address";
        }
        catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "profile-user/edit-address";
        }
    }

    @PostMapping("/profile/address/add")
    public String saveAddress(Address address, BindingResult errors, @AuthenticationPrincipal UserPrincipal loggedUser,
                              RedirectAttributes redirectAttributes, Model model) {
        Integer id = loggedUser.getId();

        try {
            User user = userService.getUserByID(id);
            if (address.getLastName().matches(".*\\d+.*")) {
                errors.rejectValue("lastName", "address", "H??? kh??ng ???????c ch???a s???!");
            }
            if (address.getLastName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
                errors.rejectValue("lastName", "address", "H??? kh??ng ???????c ch???a k?? t??? ?????c bi???t!");
            }
            if (address.getFirstName().matches(".*\\d+.*")) {
                errors.rejectValue("firstName", "address", "T??n kh??ng ???????c ch???a s???!");
            }
            if (address.getFirstName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
                errors.rejectValue("firstName", "address", "T??n kh??ng ???????c ch???a k?? t??? ?????c bi???t!");
            }
            if (address.getLastName().length() > 100) {
                errors.rejectValue("lastName", "address", "H??? kh??ng ???????c d??i qu?? 100 k?? t???!");
            }
            if (address.getFirstName().length() > 50) {
                errors.rejectValue("firstName", "address", "T??n kh??ng ???????c d??i qu?? 100 k?? t???!");
            }
            if (address.getEmail().length() > 100) {
                errors.rejectValue("email", "address", "Email kh??ng ???????c d??i qu?? 100 k?? t???!");
            }
            if (!address.getPhone().matches("\\d{10,}")) {
                errors.rejectValue("phone", "address", "S??? ??i???n tho???i kh??ng h???p l???!");
            }
            if(address.getSpecificAddress().length() > 200) {
                errors.rejectValue("lastName", "address", "?????a ch??? c??? th??? kh??ng ???????c d??i qu?? 200 k?? t???!");
            }
            if(errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.address", errors);
                redirectAttributes.addFlashAttribute("address", address);

                return "redirect:/profile/address/add";

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
                redirectAttributes.addFlashAttribute("messageSuccess", "?????a ch??? ???? ???????c l??u th??nh c??ng.");
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
            redirectAttributes.addFlashAttribute("messageSuccess", "?????a ch??? ???? ???????c ?????t l??m m???c ?????nh.");
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
            redirectAttributes.addFlashAttribute("messageSuccess", "?????a ch??? ???? b??? x??a.");
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
            model.addAttribute("user", user);
            model.addAttribute("isEdit", true);

            if (!model.containsAttribute("address")) {
                Address address = addressService.getAddress(addressId, id);
                model.addAttribute("address", address);
            }
            return "profile-user/edit-address";
        }
        catch (UserNotFoundException | AddressNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "profile-user/edit-address";
        }
    }

    @PostMapping("/profile/address/edit/{id}")
    public String saveEditAddress(Address address, BindingResult errors, @AuthenticationPrincipal UserPrincipal loggedUser,
                                  RedirectAttributes redirectAttributes, Model model, @PathVariable("id") Integer addressId) {
        Integer id = loggedUser.getId();

        try {
            User user = userService.getUserByID(id);
            Address addressExist = addressService.getAddress(addressId, id);
            if (address.getLastName().matches(".*\\d+.*")) {
                errors.rejectValue("lastName", "address", "H??? kh??ng ???????c ch???a s???!");
            }
            if (address.getLastName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
                errors.rejectValue("lastName", "address", "H??? kh??ng ???????c ch???a k?? t??? ?????c bi???t!");
            }
            if (address.getFirstName().matches(".*\\d+.*")) {
                errors.rejectValue("firstName", "address", "T??n kh??ng ???????c ch???a s???!");
            }
            if (address.getFirstName().matches(".*[:;/{}*<>=()!.#$@_+,?-]+.*")) {
                errors.rejectValue("firstName", "address", "T??n kh??ng ???????c ch???a k?? t??? ?????c bi???t!");
            }
            if (address.getLastName().length() > 100) {
                errors.rejectValue("lastName", "address", "H??? kh??ng ???????c d??i qu?? 100 k?? t???!");
            }
            if (address.getFirstName().length() > 50) {
                errors.rejectValue("firstName", "address", "T??n kh??ng ???????c d??i qu?? 100 k?? t???!");
            }
            if (address.getEmail().length() > 100) {
                errors.rejectValue("email", "address", "Email kh??ng ???????c d??i qu?? 100 k?? t???!");
            }
            if (!address.getPhone().matches("\\d{10,}")) {
                errors.rejectValue("phone", "address", "S??? ??i???n tho???i kh??ng h???p l???!");
            }
            if(address.getSpecificAddress().length() > 200) {
                errors.rejectValue("lastName", "address", "?????a ch??? c??? th??? kh??ng ???????c d??i qu?? 200 k?? t???!");
            }
            if(errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.address", errors);
                redirectAttributes.addFlashAttribute("address", address);
                return "redirect:/profile/address/edit/" + id;
            }
            else {
                System.out.println("aaaa");
                address.setUser(addressExist.getUser());
                address.setDefault(addressExist.isDefault());
                addressService.save(address);
                redirectAttributes.addFlashAttribute("messageSuccess", "?????a ch??? ???? ???????c l??u th??nh c??ng.");
                return "redirect:/profile/address";
            }
        }
        catch (UserNotFoundException | AddressNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/profile/address";
        }
    }


}
