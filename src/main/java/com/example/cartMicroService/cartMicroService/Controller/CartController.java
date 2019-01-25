package com.example.cartMicroService.cartMicroService.Controller;

import com.example.cartMicroService.cartMicroService.CartService.CartService;
import com.example.cartMicroService.cartMicroService.DTO.*;
import com.example.cartMicroService.cartMicroService.Entity.CartEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;
    @Value("${inventory.path}")
    String inventoryPath;
    @Value("${orderHistory.path}")
    String orderHistoryPath;
    @Value("${merchant.path}")
    String merchantPath;

    @PostMapping("/addCart")
    public JSONObject addCart(@RequestBody CartDTO cartDTO) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        System.out.println(cartDTO.toString());
        try {
            if (cartDTO.getToken().isEmpty()) {
                jsonObject.put("status", false);
                jsonObject.put("msg", "Invalid Access");
                return jsonObject;
            }
            CartEntity cartEntity = new CartEntity();
            BeanUtils.copyProperties(cartDTO, cartEntity);

            if (cartService.addCart(cartEntity) == -1) {

                jsonObject.put("status", false);
                jsonObject.put("message", "Field Value Empty");
                return jsonObject;
            }

            jsonObject.put("status", true);
            jsonObject.put("message", "Data Inserted");
            return jsonObject;


        } catch (Exception e) {
            jsonObject.put("status", false);
            jsonObject.put("message", "Data Insertion Failed");
            return jsonObject;
        }

    }


//    @GetMapping("/selectCart")
//    public CartEntity getCart(@RequestParam String cartId) {
//     return cartService.getCart(cartId);
//    }
//
//    @PutMapping("/updateCart")
//    public int updateCart(@RequestBody CartDTO CartDTO)
//    {
//        CartEntity cartEntity = new CartEntity();
//        BeanUtils.copyProperties(CartDTO, cartEntity);
//        cartService.updateCart(cartEntity);
//        return HttpServletResponse.SC_OK;
//    }
//
//    @DeleteMapping("/deleteCart")
//    public int deletecart(@RequestParam String cartId)
//    {
//        cartService.deleteCart(cartId);
//        return HttpServletResponse.SC_OK;
//
//    }
//


    @GetMapping("/showCart")
    public JSONObject getByToken(@RequestParam String token) throws Exception {
        cartService.getByToken(token);
        if (token.isEmpty())
            throw new Exception("invalid Access");
        List<CartEntity> cartEntityList = cartService.getByToken(token);
        CartEntityWrapper cartEntityWrapper = new CartEntityWrapper();
        cartEntityWrapper.setCartEntityList(cartEntityList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", true);
        jsonObject.put("data", cartEntityWrapper);
        jsonObject.put("price", cartService.calculateSubtotal(token));
        return jsonObject;
    }


    @Transactional(readOnly = true, rollbackFor = {RuntimeException.class, Exception.class})
    @DeleteMapping("/buy")
    public JSONObject deletetoken(@RequestParam String token) throws JSONException {
        System.out.println("connected");
        try {
            //Getting user Cart List
            if (token.isEmpty())
                throw new Exception("Invalid Access");
            List<CartEntity> cartEntityList = cartService.getByToken(token);
            if (cartEntityList.isEmpty()) {
                throw new Exception("Cart Empty");
            }


            //Setting Quantity Checker class object
            List<CartCheckQuantityDTO> cartCheckQuantityDTOList = new ArrayList<>();
            for (CartEntity cartEntity : cartEntityList) {
                CartCheckQuantityDTO cartCheckQuantityDTO = new CartCheckQuantityDTO();
                cartCheckQuantityDTO.setProductId(cartEntity.getProductId());
                cartCheckQuantityDTO.setMerchantId(cartEntity.getMerchantId());
                cartCheckQuantityDTO.setQuantity(cartEntity.getQuantity());
                cartCheckQuantityDTO.setStatus(false);
                cartCheckQuantityDTOList.add(cartCheckQuantityDTO);


            }

//             calling quantity checker api
            System.out.println(inventoryPath);
            System.out.println("afgas");
            RestTemplate restTemplate = new RestTemplate();
            System.out.println(cartCheckQuantityDTOList.toString());
//             CartQuantityWrapperDTO cartQuantityWrapperDTO =restTemplate.postForObject("inventoryPath +/inventory/checkStock", cartCheckQuantityDTOList, CartQuantityWrapperDTO.class);
            CartQuantityWrapperDTO cartQuantityWrapperDTO = restTemplate.postForObject(inventoryPath + "/inventory/checkStock", cartCheckQuantityDTOList, CartQuantityWrapperDTO.class);
            System.out.println(cartCheckQuantityDTOList.toString());
            System.out.println(cartQuantityWrapperDTO.toString());

            for (CartCheckQuantityDTO cartCheckQuantityDTO : cartQuantityWrapperDTO.getCartCheckQuantityDtoList()) {
                if (!cartCheckQuantityDTO.getStatus())
                    throw new Exception("No Stock for Product =" + cartService.getByMerchantIdAndProductid(cartCheckQuantityDTO.getMerchantId(), cartCheckQuantityDTO.getProductId()).getProductName());
            }


            //setting OrderHistory Class object
            OrderHistoryWrapper orderHistoryWrapper = new OrderHistoryWrapper();
            List<OrderHistoryDTO> orderHistoryDTOList = new ArrayList<>();
            for (CartEntity cartEntity : cartEntityList) {
                OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
                {
                    orderHistoryDTO.setImageUrl(cartEntity.getImageUrl());
                    orderHistoryDTO.setMerchantName(cartEntity.getMerchantName());
                    orderHistoryDTO.setPrice(cartEntity.getPrice());
                    orderHistoryDTO.setUserId(cartEntity.getToken());
                    orderHistoryDTO.setProductName(cartEntity.getProductName());
                    orderHistoryDTO.setQuantity(cartEntity.getQuantity());
                    orderHistoryDTO.setMerchantId(cartEntity.getMerchantId());
                    orderHistoryDTO.setProductId(cartEntity.getProductId());
                }
                //copying cartEntity -> orderHistoryDTO
                orderHistoryDTOList.add(orderHistoryDTO);
            }
            orderHistoryWrapper.setOrderHistoryDTOList(orderHistoryDTOList);
//            Calling OrderHistory update Api
//            String jsonString = restTemplate.postForObject(orderHistoryPath + "/user/insertOrderHistory",orderHistoryWrapper,String.class);
            String jsonString = restTemplate.postForObject(orderHistoryPath + "/user/insertOrderHistory", orderHistoryWrapper, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject orderHistoryJsonResponse = objectMapper.readValue(jsonString, JSONObject.class);
            if (!(Boolean) orderHistoryJsonResponse.get("status"))
                throw new Exception("order History Not Updated");


//            Adding MerchantID list Whose ProductSold needs to be incremented
            List<String> merchantIdList = new ArrayList<>();
            for (CartEntity cartEntity : cartEntityList) {
                merchantIdList.add(cartEntity.getMerchantId());
            }

            //calling product sold increment api for all the required merchants

//            JSONObject productSoldUpdateResponse = restTemplate.postForObject( merchantPath + "/merchant/updateProductSold",merchantIdList,JSONObject.class);
            JSONObject productSoldUpdateResponse = restTemplate.postForObject(merchantPath + "/merchant/updateProductSold", merchantIdList, JSONObject.class);


            System.out.println(productSoldUpdateResponse.toString());
            if (productSoldUpdateResponse.get("status").equals("false")) {
                throw new Exception("Product Sold  Increment Error");
            }


            String toAddress = restTemplate.postForObject(orderHistoryPath + "/user/getemail", token, String.class);
//            String toAddress=restTemplate.postForObject(orderHistoryPath+ "/user/getemail",token,String.class);
            System.out.println(toAddress);
//            String toAddress="adersh.g.97@gmail.com";
            if (toAddress.isEmpty())
                throw new Exception("User Does not Exist");
            Boolean mailResponse = cartService.sendEmail(toAddress, cartService.getMessageBody(cartEntityList));

            if (!mailResponse) {
                throw new Exception("Error Sending Email");
            }

            cartService.deletetoken(token);


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("Status", true);
            jsonObject2.put("Message", "Cart Cleared , Purchase successful , OrderHistory updated");
            return jsonObject2;

        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", false);
            jsonObject.put("Msg", e.getMessage());
            return jsonObject;
        }

    }


    @DeleteMapping("/removeProduct")
    public CartEntityWrapper deleteProductAndUserId(@RequestParam String userId, @RequestParam String productId) throws JSONException {
        try {
            if (userId.isEmpty())
                throw new Exception("Invalid Access");
            CartEntityWrapper cartEntityWrapper = new CartEntityWrapper();
            cartEntityWrapper.setCartEntityList(cartService.getByToken(userId));
            return cartEntityWrapper;
        } catch (Exception e) {
            return null;
        }
    }


    @PutMapping("/updateQuantity")
    public JSONObject updateQuantity(@RequestParam String token, @RequestParam String cartId, @RequestParam Integer quantity) {
        CartEntity cartEntity = cartService.getCart(cartId);
        System.out.println(cartEntity.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            if (!cartEntity.getToken().equals(token)) {
                throw new Exception("Invalid Access");
            }
            if (quantity == 0) {
                cartService.deleteCart(cartId);
            } else {
                cartEntity.setQuantity(quantity);
                RestTemplate restTemplate = new RestTemplate();
                CartCheckQuantityDTO cartCheckQuantityDTO = new CartCheckQuantityDTO();
                {
                    cartCheckQuantityDTO.setMerchantId(cartEntity.getMerchantId());
                    cartCheckQuantityDTO.setProductId(cartEntity.getProductId());
                    cartCheckQuantityDTO.setQuantity(cartEntity.getQuantity());
                    cartCheckQuantityDTO.setStatus(false);
                    cartCheckQuantityDTO.setMsg(null);
                }

                jsonObject = restTemplate.postForObject(inventoryPath + "/inventory/checkStockOneProduct", cartCheckQuantityDTO, JSONObject.class);
                System.out.println(jsonObject.toString());

                if (!(Boolean) jsonObject.get("status"))
                    throw new Exception((String) jsonObject.get("msg"));
                cartService.updateCart(cartEntity);

            }
            jsonObject.put("status", true);
            jsonObject.put("msg", quantity);
            jsonObject.put("price", cartService.calculateSubtotal(token));


            return jsonObject;

        } catch (Exception e) {
            cartEntity.setQuantity((Integer) jsonObject.get("stockAvailable"));
            System.out.println(e.getMessage());
            jsonObject.put("status", false);
            jsonObject.put("msg", jsonObject.get("stockAvailable"));
            jsonObject.put("price", cartService.calculateSubtotal(token));
            return jsonObject;
        }
    }


}




