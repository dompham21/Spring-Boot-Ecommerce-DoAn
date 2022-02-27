package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {
    @Query("SELECT p FROM Province p ORDER BY p.name")
    List<Province> findAll();
}
