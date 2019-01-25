package com.example.cartMicroService.cartMicroService.DTO;

public class CartCheckQuantityDTO {
    private String merchantId;
    private String productId;
    private Integer quantity;
    private Boolean status;
    private String msg;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CartCheckQuantityDTO{" +
                "merchantId='" + merchantId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
