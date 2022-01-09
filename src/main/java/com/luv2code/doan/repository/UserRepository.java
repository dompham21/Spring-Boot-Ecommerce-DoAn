package com.luv2code.doan.repository;

import com.luv2code.doan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("Select u from User u WHere u.verificationCode = :code")
    public User findByVerificationCode(String code);

    @Query("Select u from User u WHere u.id = :id")
    public User getUserByID(int id);

    @Query("Select u from User u WHere u.email = :email")
    public User getUserByEmail(String email);
}
