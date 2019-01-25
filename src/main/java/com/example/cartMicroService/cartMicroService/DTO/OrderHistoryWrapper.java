package com.example.cartMicroService.cartMicroService.DTO;
import java.util.List;

public class OrderHistoryWrapper {

    private List<OrderHistoryDTO> orderHistoryDTOList;


    public List<OrderHistoryDTO> getOrderHistoryDTOList() {
        return orderHistoryDTOList;
    }

    public void setOrderHistoryDTOList(List<OrderHistoryDTO> orderHistoryDTOList) {
        this.orderHistoryDTOList = orderHistoryDTOList;
    }

    @Override
    public String toString() {
        return "OrderHistoryWrapper{" +
                "orderHistoryDTOList=" + orderHistoryDTOList +
                '}';
    }
}



