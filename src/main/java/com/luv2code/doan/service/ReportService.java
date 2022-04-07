package com.luv2code.doan.service;

import com.luv2code.doan.bean.Items;
import com.luv2code.doan.repository.OrderStatusRepository;
import com.luv2code.doan.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;


    public List<Object> reportOrder() {
        List<Object> listArray = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", orderStatusRepository.getOrderStatusById(i).getName());
            mapItem.put("y", reportRepository.countOrderByStatus(i));
            listArray.add(mapItem);
        }
        return listArray;
    }

    public long countOrderByWeek(){
        return reportRepository.countOrderByWeek();
    }

    public long countUserByWeek(){
        return reportRepository.countUserByWeek();
    }

    public long countReviewByWeek(){
        return reportRepository.countReviewByWeek();
    }

    public long getTotalOrderByWeek(){
        return reportRepository.totalOrderByWeek();
    }

    public List<Items> reportReceipt() {
        List<Items> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Date d = subDays(new Date(), i);
            Items myItem = new Items();
            myItem.setName(convertDayToString(d));
            myItem.setValue(reportRepository.countOrderByDate(d));
            list.add(myItem);
        }
        return list;
    }

    public List<Items> reportEarn() {
        List<Items> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Date d = subDays(new Date(), i);
            Items myItem = new Items();
            myItem.setName(convertDayToString(d));
            myItem.setValue((long) reportRepository.totalEarnByDate(d));
            list.add(myItem);
        }


        return list;
    }

    public List<Items> reportUser() {
        List<Items> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Date d = subDays(new Date(), i);
            Items myItem = new Items();
            myItem.setName(convertDayToString(d));
            myItem.setValue(reportRepository.countUserByDate(d));
            System.out.println(myItem.getValue());
            list.add(myItem);
        }
        return list;
    }

    public Date addDays(Date date, int days) {
        GregorianCalendar cal =  new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public Date subDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    private String convertDayToString(Date date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }
}
