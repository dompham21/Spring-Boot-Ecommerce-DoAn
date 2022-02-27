package com.luv2code.doan.service;

import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Integer addProductToCart(Product product, User user, Integer quantity) {
        Integer updatedQuantity = quantity;
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if(cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
        }
        else {
            cartItem = new Cart();
            cartItem.setProducts(product);
            cartItem.setUser(user);
        }

        cartItem.setQuantity(updatedQuantity);

        cartRepository.save(cartItem);
        return updatedQuantity;
    }

    public List<Cart> findCartByUser(Integer id) {
        return cartRepository.findByUserId(id);
    }

    public double updatedQuantity(Product product, User user, Integer quantity) {
        cartRepository.updateQuantity(quantity, user.getId(), product.getId());

        return product.getPrice() * quantity;
    }

    public void deleteCartItem(Integer userId, Integer productId) {
        cartRepository.deleteByUserAndProduct(userId, productId);
    }

    public void deleteCartItemByUser(Integer userId) {
        cartRepository.deleteByUser(userId);
    }
}


