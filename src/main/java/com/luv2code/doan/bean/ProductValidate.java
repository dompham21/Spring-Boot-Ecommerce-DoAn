package com.luv2code.doan.bean;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;


public class ProductValidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên không được bỏ trống!")
    private String name;

    @NotBlank(message = "Description không được bỏ trống!")
    private String description;

    @NotBlank(message = "Short description không được bỏ trống!")
    private String shortDescription;

    @NotNull(message = "Price không được bỏ trống!")
    @Min(value = 0, message = "Gia phải lon hon hoac bang 0!")
    private Double price;

    @NotNull(message = "Image không được bỏ trống!")
    private MultipartFile image;

    @Min(value = 0, message = "Discount  phải lon hon hoac bang 0!")
    @Max(value = 100, message = "Discount phai be hon hoac bang 100!")
    private Integer discount;

    @NotNull(message = "In stock không được bỏ trống!")
    @Min(value = 0, message = "In stock so luong phải lon hon hoac bang 0!")
    private Integer inStock;

    private Boolean isActive = true;



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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }


    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
