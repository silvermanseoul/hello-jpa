package io.silverman.hellojpa.dto.order.query;

import io.silverman.hellojpa.domain.Address;
import io.silverman.hellojpa.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderQueryFlatDto {

    private Long orderId;
    private String memberName;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    private String itemName;
    private int orderPrice;
    private int count;

    public OrderQueryFlatDto(Long orderId, String memberName, LocalDateTime orderDate, OrderStatus orderStatus, Address address, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.memberName = memberName;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;

        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
