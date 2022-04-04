package com.luv2code.doan.service;

import com.luv2code.doan.entity.OrderStatus;
import com.luv2code.doan.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusService {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    public List<OrderStatus> listOrderStatus() {
        return orderStatusRepository.findAll();
    }
}
