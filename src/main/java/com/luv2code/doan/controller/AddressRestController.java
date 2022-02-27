package com.luv2code.doan.controller;


import com.luv2code.doan.entity.District;
import com.luv2code.doan.entity.Ward;
import com.luv2code.doan.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AddressRestController {
    private final Logger log = LoggerFactory.getLogger(AddressRestController.class);

    @Autowired
    private AddressService addressService;


    @GetMapping("/api/district/{provinceCode}")
    public List<District> getDistricts(@PathVariable("provinceCode") String code) {
        return addressService.getListDistrictByProvinceCode(code);
    }

    @GetMapping("/api/ward/{districtCode}")
    public List<Ward> getWards(@PathVariable("districtCode") String code) {
        return addressService.getListWardByDistrictCode(code);
    }

}
