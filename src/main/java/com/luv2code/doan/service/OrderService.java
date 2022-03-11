package com.luv2code.doan.service;


import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.OrderNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    public static final int ORDERS_PER_PAGE = 5;

    @Autowired
    private OrderRepository orderRepository;


    public Order createOrder(User user, Address address, List<Cart> cartList) {
        Order newOrder = new Order();
        newOrder.setDate(new java.util.Date());
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

    public Page<Order> listForUserByPage(User user, int pageNum, String keyword,
                                         Date startDate, Date endDate, String status) {

        Sort sort = Sort.by("date");
        sort = sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findByKeyword(keyword, user.getId(), startDate, endDate, status, pageable);
        }

        return orderRepository.findAll(user.getId(), startDate, endDate, status, pageable);

    }


    public Order getOrder(Integer id, User user) throws OrderNotFoundException {
        try {
            return orderRepository.findByIdAndUser(id, user.getId());


        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }


}
