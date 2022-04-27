package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface ReportRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT count(o.id) FROM Order o WHERE o.status.id = :statusId")
    public long countOrderByStatus(int statusId);

    @Query("SELECT count(o.id) FROM Order o WHERE o.date = :date")
    public long countOrderByDate(Date date);

    @Query("SELECT count(u.id) FROM User u WHERE u.registrationDate = :date")
    public long countUserByDate(Date date);

    @Query("SELECT COALESCE(sum(o.totalPrice),0) FROM Order o WHERE o.date = :date AND o.status.id = 4")
    public double totalEarnByDate(Date date);

    @Query(value = "SELECT count(*)\n" +
            "FROM   orders\n" +
            "WHERE  YEARWEEK(`date`, 1) = YEARWEEK(CURDATE(), 1) and status_id = 1", nativeQuery = true)
    public long countOrderByWeek();

    @Query(value = "SELECT count(*)\n" +
            "FROM   users\n" +
            "WHERE  YEARWEEK(`created_at`, 1) = YEARWEEK(CURDATE(), 1)", nativeQuery = true)
    public long countUserByWeek();

    @Query(value ="SELECT count(*) FROM review WHERE  YEARWEEK(`date`, 1) = YEARWEEK(CURDATE(), 1)", nativeQuery = true)
    public long countReviewByWeek();

    @Query(value = "SELECT COALESCE(sum(total_price),0) FROM orders WHERE  YEARWEEK(`date`, 1) = YEARWEEK(CURDATE(), 1) AND status_id = 4", nativeQuery = true)
    public long totalOrderByWeek();

}
