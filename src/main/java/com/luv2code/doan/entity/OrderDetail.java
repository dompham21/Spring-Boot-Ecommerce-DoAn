package com.luv2code.doan.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "order_detail")
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order orders;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product products;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "item_price")
    private Double itemPrice;

    public Order getOrders() {
        return orders;
    }

    public void setOrders(Order orders) {
        this.orders = orders;
    }

    public Product getProducts() {
        return products;
    }

    public void setProducts(Product products) {
        this.products = products;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
