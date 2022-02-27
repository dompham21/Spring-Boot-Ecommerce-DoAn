package com.luv2code.doan.entity;



import javax.persistence.*;

@Entity
@Table(name = "wards")
public class Ward {
    @Id
    @Column(name="code", length = 20, nullable = false)
    private String code;

    @Column(name="name", nullable = false, length = 255)
    private String name;

    @Column(name="name_en", length = 255)
    private String nameEn;

    @Column(name="full_name_en", length = 255)
    private String fullNameEn;

    @Column(name="full_name", length = 255)
    private String fullName;

    @Column(name="code_name", length = 255)
    private String codeName;

    @ManyToOne
    @JoinColumn(name="district_code")
    private District district;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getFullNameEn() {
        return fullNameEn;
    }

    public void setFullNameEn(String fullNameEn) {
        this.fullNameEn = fullNameEn;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

}
