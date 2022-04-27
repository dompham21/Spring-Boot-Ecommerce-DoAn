package com.luv2code.doan.exceptions;

public class CartMoreThanProductInStock extends Exception{
    public CartMoreThanProductInStock(String message) {
        super(message);
    }
}
