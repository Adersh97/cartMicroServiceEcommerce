package com.example.cartMicroService.cartMicroService.CartService;

import com.example.cartMicroService.cartMicroService.DTO.CartDTO;
import com.example.cartMicroService.cartMicroService.Entity.CartEntity;

import javax.mail.MessagingException;
import java.util.List;

public interface CartService {
    public int addCart(CartEntity cartEntity);
    public CartEntity getCart(String cartId);
    public int updateCart(CartEntity cartEntity);
    public int deleteCart(String cartId);
    public List<CartEntity> getByToken(String token);
    public int deletetoken(String token);
    public int deleteProductId(String token,String productId);
    public CartEntity getByMerchantIdAndProductid(String merchantId, String productId);
    public Boolean existsCartEntityByTokenAndProductId(String token,String productId,String merchantId);
    public CartEntity findByTokenAndMerchantIdAndProductId(String token,String merchantId,String productId);
    public Double calculateSubtotal(String token);
    public Boolean sendEmail(String toAddress,String message) throws MessagingException ;
    public String getMessageBody(List<CartEntity> cartEntityList);
    public Boolean cartEntityCheckNull(CartEntity cartEntity);


}
