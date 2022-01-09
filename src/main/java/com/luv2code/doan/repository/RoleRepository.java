package com.luv2code.doan.repository;


import com.luv2code.doan.entity.Role;
import com.luv2code.doan.entity.User;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT r FROM Role r where  r.id = :id")
    public Role getRoleByID(int id);
}
