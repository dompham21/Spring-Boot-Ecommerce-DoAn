package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {

    @Query("SELECT w FROM Ward w WHERE w.district.code = :districtCode ORDER BY w.name")
    List<Ward> findWardByDistrictCode(String districtCode);
}
