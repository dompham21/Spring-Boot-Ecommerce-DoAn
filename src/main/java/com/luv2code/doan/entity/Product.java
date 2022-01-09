package com.luv2code.doan.entity;


import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Tên không được bỏ trống!")
    @Column(name="name", unique = true, length = 255, nullable = false)
    private String name;

    @NotBlank(message = "Description không được bỏ trống!")
    @Column(name = "description", nullable = false)
    private String description;

    @NotBlank(message = "Short description không được bỏ trống!")
    @Column(name = "short_description", nullable = false)
    private String shortDescription;

    @NotNull(message = "Price không được bỏ trống!")
    @Min(value = 0, message = "Gia phải lon hon hoac bang 0!")
    @Column(name = "price", nullable = false)
    private Double price;

    @NotEmpty(message = "Image không được bỏ trống!")
    @Column(name = "image", nullable = false)
    private String image;

    @Min(value = 0, message = "Discount  phải lon hon hoac bang 0!")
    @Max(value = 100, message = "Discount phai be hon hoac bang 100!")
    @Column(name = "discount")
    private Integer discount;

    @NotNull(message = "In stock không được bỏ trống!")
    @Min(value = 0, message = "In stock so luong phải lon hon hoac bang 0!")
    @Column(name = "in_stock", nullable = false)
    private Integer inStock;

    @Column(name = "sold_quantity")
    private Integer soldQuantity;


    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd/MM/yyyy")
    @Column(name="created_at", nullable = false)
    private Date registrationDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categories;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Collection<Review> reviews;

    @OneToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private Collection<Cart> carts;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brands;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Collection<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Collection<Review> reviews) {
        this.reviews = reviews;
    }

    public Collection<Cart> getCarts() {
        return carts;
    }

    public void setCarts(Collection<Cart> carts) {
        this.carts = carts;
    }

    public Category getCategories() {
        return categories;
    }

    public void setCategories(Category categories) {
        this.categories = categories;
    }

    public Brand getBrands() {
        return brands;
    }

    public void setBrands(Brand brands) {
        this.brands = brands;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sortDescription='" + shortDescription + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", discount=" + discount +
                ", inStock=" + inStock +
                ", soldQuantity=" + soldQuantity +
                ", registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                ", categories=" + categories +
                ", reviews=" + reviews +
                ", carts=" + carts +
                ", brands=" + brands +
                '}';
    }
}
