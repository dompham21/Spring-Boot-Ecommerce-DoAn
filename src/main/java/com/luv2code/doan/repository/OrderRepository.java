package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Order;
import com.luv2code.doan.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
            + "WHERE o.user.id = :userId AND p.name LIKE %:keyword% AND " +
            "o.date >= case when :startDate IS NULL THEN (SELECT MIN(o.date) FROM Order  o) else :startDate end " +
            "AND o.date <= case when :endDate IS NULL THEN (SELECT MAX(o.date) FROM Order  o) else :endDate end AND " +
            "(( :status = 'ALL' AND o.status <> '') OR (:status <> 'ALL' AND o.status = :status))")
    public Page<Order> findByKeyword(String keyword, Integer userId, Date startDate, Date endDate, String status, Pageable pageable);


    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND " +
            "o.date >= case when :startDate IS NULL THEN (SELECT MIN(o.date) FROM Order  o) else :startDate end " +
            "AND o.date <= case when :endDate IS NULL THEN (SELECT MAX(o.date) FROM Order  o) else :endDate end AND " +
            "((:status = 'ALL' AND o.status <> '') OR (:status <> 'ALL' AND o.status = :status))")
    public Page<Order> findAll(Integer userId, Date startDate, Date endDate, String status, Pageable pageable);


    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
            + "WHERE p.name LIKE %:keyword% AND " +
            "o.date >= case when :startDate IS NULL THEN (SELECT MIN(o.date) FROM Order  o) else :startDate end " +
            "AND o.date <= case when :endDate IS NULL THEN (SELECT MAX(o.date) FROM Order  o) else :endDate end AND " +
            "(( :status = 'ALL' AND o.status <> '') OR (:status <> 'ALL' AND o.status = :status))")
    public Page<Order> findAdminByKeyword(String keyword, Date startDate, Date endDate, String status, Pageable pageable);


    @Query("SELECT o FROM Order o WHERE " +
            "o.date >= case when :startDate IS NULL THEN (SELECT MIN(o.date) FROM Order  o) else :startDate end " +
            "AND o.date <= case when :endDate IS NULL THEN (SELECT MAX(o.date) FROM Order  o) else :endDate end AND " +
            "((:status = 'ALL' AND o.status <> '') OR (:status <> 'ALL' AND o.status = :status))")
    public Page<Order> findAdminAll(Date startDate, Date endDate, String status, Pageable pageable);


    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.id = :id")
    public Order findByIdAndUser(Integer id, Integer userId);

    @Query("SELECT o FROM Order o WHERE o.id = :id")
    public Order findByOrderId(Integer id);

    @Query("Select o from OrderDetail o WHERE  o.order.id = :id")
    public List<OrderDetail> getOrderDetail(Integer id);

    @Query(value = "select count(*) from (Select id, status_id From orders where user_id = :userId) AS ref inner join order_detail AS od\n" +
            "on ref.id = od.order_id and od.product_id = :productId and ref.status_id = 4", nativeQuery = true)
    public long countOrderByProductAndUser(Integer userId, Integer productId);
}
