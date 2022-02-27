package com.luv2code.doan;

import com.luv2code.doan.entity.District;
import com.luv2code.doan.entity.Province;
import com.luv2code.doan.entity.Ward;
import com.luv2code.doan.repository.DistrictRepository;
import com.luv2code.doan.repository.ProvinceRepository;
import com.luv2code.doan.repository.WardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProvinceControllerTest {
    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;

    @Test
    public void testListProvinces() {
        List<Province> listProvinces = provinceRepository.findAll();
        for(Province p : listProvinces) {
            System.out.println(p.getCode());
        }

        System.out.println(listProvinces.size());
        assertThat(listProvinces.size()).isGreaterThan(0);
    }

    @Test
    public void testListDistrictByProvinceCode() {
        List<District> listDistricts = districtRepository.findDistrictByProvinceCode("89");
        for(District d : listDistricts) {
            System.out.println(d.getCode());
        }

        System.out.println(listDistricts.size());
        assertThat(listDistricts.size()).isGreaterThan(0);

    }

    @Test
    public void testListWardByDistrictCode() {
        List<Ward> listWards = wardRepository.findWardByDistrictCode("883");
        for(Ward w : listWards) {
            System.out.println(w.getName());
        }

        System.out.println(listWards.size());
        assertThat(listWards.size()).isGreaterThan(0);

    }


}

