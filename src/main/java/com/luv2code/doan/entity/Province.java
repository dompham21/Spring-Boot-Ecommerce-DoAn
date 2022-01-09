package com.luv2code.doan.entity;

import javax.persistence.*;
import java.util.Collection;


@Entity
@Table(name = "province")
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="name", nullable = false, length = 45)
    private String name;

    @Column(name="code")
    private String code;

    @OneToMany(mappedBy = "province", fetch = FetchType.LAZY)
    private Collection<District> districts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Collection<District> getDistricts() {
        return districts;
    }

    public void setDistricts(Collection<District> districts) {
        this.districts = districts;
    }
}
