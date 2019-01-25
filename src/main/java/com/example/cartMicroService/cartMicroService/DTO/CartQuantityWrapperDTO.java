package com.example.cartMicroService.cartMicroService.DTO;

import java.util.List;

public class CartQuantityWrapperDTO {
    private List<CartCheckQuantityDTO> cartCheckQuantityDtoList;

    public List<CartCheckQuantityDTO> getCartCheckQuantityDtoList() {
        return cartCheckQuantityDtoList;
    }

    public void setCartCheckQuantityDtoList(List<CartCheckQuantityDTO> cartCheckQuantityDtoList) {
        this.cartCheckQuantityDtoList = cartCheckQuantityDtoList;
    }

    @Override
    public String toString() {
        return "CartQuantityWrapperDTO{" +
                "cartCheckQuantityDtoList=" + cartCheckQuantityDtoList +
                '}';
    }
}
