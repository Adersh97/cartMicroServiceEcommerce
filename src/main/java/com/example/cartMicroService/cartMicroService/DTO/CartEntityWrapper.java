package com.example.cartMicroService.cartMicroService.DTO;

import com.example.cartMicroService.cartMicroService.Entity.CartEntity;

import java.util.ArrayList;
import java.util.List;

public class CartEntityWrapper {
    private List<CartEntity> cartEntityList=new ArrayList<>();

    public List<CartEntity> getCartEntityList() {
        return cartEntityList;
    }

    public void setCartEntityList(List<CartEntity> cartEntityList) {
        this.cartEntityList = cartEntityList;
    }

    @Override
    public String toString() {
        return "CartEntityWrapper{" +
                "cartEntityList=" + cartEntityList +
                '}';
    }
}
