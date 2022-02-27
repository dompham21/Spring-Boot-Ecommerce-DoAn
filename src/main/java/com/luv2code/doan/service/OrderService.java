package com.luv2code.doan.service;


import com.luv2code.doan.entity.*;
import com.luv2code.doan.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;


    public Order createOrder(User user, Address address, List<Cart> cartList) {
        Order newOrder = new Order();
        newOrder.setDate(new Date());
        newOrder.setAddress(address);
        newOrder.setUser(user);

        Set<OrderDetail> orderDetailSet = newOrder.getOrderDetails();
        double totalWithoutDiscount = 0.0;
        for (Cart cart : cartList) {
            Product product = cart.getProducts();
            OrderDetail orderDetail = new OrderDetail();


            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cart.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice()); //price with discount
            orderDetail.setItemPrice(product.getPrice());
            orderDetail.setSubTotal(cart.getSubtotal());
            totalWithoutDiscount += cart.getSubtotal();

            orderDetailSet.add(orderDetail);
        }
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setTotalPrice(totalWithoutDiscount);

        return orderRepository.save(newOrder);
    }

}
