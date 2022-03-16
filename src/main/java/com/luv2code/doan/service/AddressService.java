package com.luv2code.doan.service;

import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.AddressNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.AddressRepository;
import com.luv2code.doan.repository.DistrictRepository;
import com.luv2code.doan.repository.ProvinceRepository;
import com.luv2code.doan.repository.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;


    public List<Province> getListProvinces() {
        return provinceRepository.findAll();
    }

    public List<District> getListDistrict() {
        return districtRepository.findAll();
    }

    public List<Ward> getListWard() {
        return wardRepository.findAll();
    }

    public List<Address> getListAddressByUserId(Integer id) {
        return addressRepository.getAddressByUserId(id);
    }

    public long getCountAddressByUserId(Integer id) {
        return addressRepository.countAddressByUserId(id);
    }

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public void setDefaultAddress(Integer addressId,Integer userId) {
        addressRepository.setDefaultAddress(addressId);
        addressRepository.setNonDefaultForOthers(addressId, userId);
    }

    public void delete(Integer addressId, Integer userId) {
        addressRepository.deleteByIdAndUserId(addressId, userId);
    }

    public Address getAddress(Integer addressId, Integer userId) throws AddressNotFoundException {
        try {
            return addressRepository.findByIdAndUserId(addressId, userId);
        }
        catch(NoSuchElementException ex) {
            throw new AddressNotFoundException("Could not find any address with ID " + addressId);

        }
    }
}
