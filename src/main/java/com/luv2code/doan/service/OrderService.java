package com.luv2code.doan.service;


import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.OrderNotFoundException;
import com.luv2code.doan.repository.OrderRepository;
import com.luv2code.doan.repository.OrderStatusRepository;
import com.luv2code.doan.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    public static final int ORDERS_PER_PAGE = 5;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private ProductRepository productRepository;


    public Order createOrder(User user, Address address, List<Cart> cartList) {
        Order newOrder = new Order();
        newOrder.setDate(new java.util.Date());
        newOrder.setAddress(address);
        newOrder.setUser(user);

        Set<OrderDetail> orderDetailSet = newOrder.getOrderDetails();
        double totalWithoutDiscount = 0.0;
        for (Cart cart : cartList) {
            Product product = cart.getProducts();
            product.setSoldQuantity(product.getSoldQuantity() + cart.getQuantity());
            product.setInStock(product.getInStock() - cart.getQuantity());
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


        newOrder.setStatus(orderStatusRepository.getOrderStatusById(1));
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

    public Page<Order> listByPage(int pageNum, String keyword,
                                  Date startDate, Date endDate, String status) {
        Sort sort = Sort.by("date");
        sort = sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findAdminByKeyword(keyword, startDate, endDate, status, pageable);
        }

        return orderRepository.findAdminAll(startDate, endDate, status, pageable);
    }


    public Order getOrder(Integer id, User user) throws OrderNotFoundException {
        try {
            return orderRepository.findByIdAndUser(id, user.getId());


        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }

    public Order getOrderById(Integer id) throws OrderNotFoundException {
        try {
            return orderRepository.findByOrderId(id);
        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }


    public void acceptOrder(Integer id, Integer statusId) throws OrderNotFoundException {
        try {
            Order order = orderRepository.findByOrderId(id);

            switch (statusId) {
                case 1: { //Neu dang cho xac nhan thi -> cho giao hang
                    order.setStatus(orderStatusRepository.getOrderStatusById(6));
                    break;
                }
                case 2: { //Neu dang cho yeu cau huy thi -> Da huy
                    List<OrderDetail> orderDetail = orderRepository.getOrderDetail(id);

                    for(OrderDetail item : orderDetail)
                    {
                        Product product = item.getProduct();
                        product.setSoldQuantity(product.getSoldQuantity() - item.getQuantity());
                        product.setInStock(product.getSoldQuantity() + item.getQuantity());
                        productRepository.save(product);
                    }
                    order.setStatus(orderStatusRepository.getOrderStatusById(5));
                    break;
                }
                case 3: { //Neu dang giao thi -> da giao
                    order.setStatus(orderStatusRepository.getOrderStatusById(4));
                    break;
                }
                case 6: { //Neu dang cho giao hang -> dang giao hang
                    order.setStatus(orderStatusRepository.getOrderStatusById(3));
                    break;
                }
                default:
                    break;
            }
            orderRepository.save(order);
        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }

    public void denyOrder(Integer id, Integer statusId) throws OrderNotFoundException {
        try {
            Order order = orderRepository.findByOrderId(id);

            switch (statusId) {
                case 1:
                case 6:
                case 3: { //Neu dang cho xac nhan, dang giao, dang cho giao thi -> huy
                    List<OrderDetail> orderDetail = orderRepository.getOrderDetail(id);

                    for(OrderDetail item : orderDetail)
                    {
                        Product product = item.getProduct();
                        product.setInStock(product.getInStock() + item.getQuantity());
                        product.setSoldQuantity(product.getSoldQuantity() - item.getQuantity());
                        productRepository.save(product);
                    }

                    order.setStatus(orderStatusRepository.getOrderStatusById(5));

                    break;
                }
                case 2: { //Neu dang yeu cau huy thi -> cho xac nhan
                    order.setStatus(orderStatusRepository.getOrderStatusById(1));
                    break;
                }
                default:
                    break;
            }
            orderRepository.save(order);
        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }
    public void requestCancel(Integer id) throws OrderNotFoundException {
        try {
            Order order = orderRepository.findByOrderId(id);
            order.setStatus(orderStatusRepository.getOrderStatusById(2));
            orderRepository.save(order);
        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }
    public void cancelRequest(Integer id) throws OrderNotFoundException {
        try {
            Order order = orderRepository.findByOrderId(id);

            order.setStatus(orderStatusRepository.getOrderStatusById(1));

            orderRepository.save(order);
        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }

    public boolean isUserHasBuyProduct(Integer userId, Integer productId) {
        long num = orderRepository.countOrderByProductAndUser(userId, productId);
        System.out.println("userId: " + userId + ", productId: " + productId + ", num: " + num);
        return num > 0;
    }

}
