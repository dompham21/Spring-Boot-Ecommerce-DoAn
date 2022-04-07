package com.luv2code.doan.bean;

public class Items {
    private String name;
    private Long value;

    public Items() {
        super();
    }
    public Items(String name, Long value) {
        super();
        this.name = name;
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
    }
}
