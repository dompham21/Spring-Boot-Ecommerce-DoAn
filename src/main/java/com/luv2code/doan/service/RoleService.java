package com.luv2code.doan.service;


import com.luv2code.doan.entity.Role;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public Role getRoleByID(int id) {
        Role role = roleRepository.getRoleByID(id);
        return role;
    }
}
