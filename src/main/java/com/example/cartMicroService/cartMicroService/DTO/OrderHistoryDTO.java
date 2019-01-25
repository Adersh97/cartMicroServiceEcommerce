package com.example.cartMicroService.cartMicroService.DTO;
public class OrderHistoryDTO {

    private String orderId;
    private String userId;
    private String productName;
    private String imageUrl;
    private String merchantName;
    private int quantity;
    private Double price;
    private String merchantId;
    private String productId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "OrderHistoryDTO{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", productName='" + productName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", merchantId='" + merchantId + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}