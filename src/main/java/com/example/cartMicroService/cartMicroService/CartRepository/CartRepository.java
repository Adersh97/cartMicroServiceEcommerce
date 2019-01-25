package com.example.cartMicroService.cartMicroService.CartRepository;

import com.example.cartMicroService.cartMicroService.Entity.CartEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends CrudRepository<CartEntity,String> {
    public List<CartEntity> findAllByToken(String token);
    public void deleteAllByToken(String token);
    public void deleteByTokenAndProductId(String token,String ProductId);
    public CartEntity findByMerchantIdAndProductId(String merchantId,String productId);
    public Boolean existsCartEntityByTokenAndProductIdAndMerchantId(String token,String productId,String merchantId);
    public CartEntity findByTokenAndMerchantIdAndProductId(String token,String merchantId,String productId);
    @Query(name="calculateSubTotal",value="SELECT SUM(PRICE*QUANTITY) FROM CART WHERE token= :token",nativeQuery = true)
    public Double calculateSubTotal(@Param("token") String token);
}
