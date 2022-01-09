package com.luv2code.doan.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "carts")
public class Cart implements Serializable {
    /**
     * Serializable convert the state of an object to a byte stream
     * can save to a database or transfer
     **/


    //We use serialVersionUID to verify that the saved and loaded objects
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product products;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "quantity")
    private Integer quantity;

    public Product getProducts() {
        return products;
    }

    public void setProducts(Product products) {
        this.products = products;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


}
