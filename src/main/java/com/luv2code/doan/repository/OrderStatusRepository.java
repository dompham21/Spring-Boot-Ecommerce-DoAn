package com.luv2code.doan.repository;

import com.luv2code.doan.entity.OrderStatus;
import com.luv2code.doan.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
    @Query("SELECT os FROM OrderStatus os where  os.id = :id")
    public OrderStatus getOrderStatusById(int id);



}
