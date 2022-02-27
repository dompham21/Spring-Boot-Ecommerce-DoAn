package com.luv2code.doan.repository;

import com.luv2code.doan.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {
    @Query("SELECT d FROM District d WHERE d.province.code = :provinceCode ORDER BY d.name")
    List<District> findDistrictByProvinceCode(String provinceCode);
}
