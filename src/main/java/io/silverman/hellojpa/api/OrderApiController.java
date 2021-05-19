package io.silverman.hellojpa.api;

import io.silverman.hellojpa.domain.Address;
import io.silverman.hellojpa.domain.Order;
import io.silverman.hellojpa.domain.OrderItem;
import io.silverman.hellojpa.domain.OrderStatus;
import io.silverman.hellojpa.dto.order.query.OrderItemQueryDto;
import io.silverman.hellojpa.dto.order.query.OrderQueryDto;
import io.silverman.hellojpa.repository.OrderRepository;
import io.silverman.hellojpa.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    //== 엔티티를 조회해서 DTO로 변환 ==//

    @GetMapping("/api/v2.0/orders")
    public ResponseWrapper<List<OrderDto>> ordersV20(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                     @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAll(offset, limit);
        return orders.stream()
                .map(OrderDto::new)
                .collect(collectingAndThen(toList(), ResponseWrapper::new));
    }

    @GetMapping("/api/v2.1/orders")
    public ResponseWrapper<List<OrderDto>> ordersV21(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                     @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        return orders.stream()
                .map(OrderDto::new)
                .collect(collectingAndThen(toList(), ResponseWrapper::new));
    }

    @GetMapping("/api/v2.2/orders")
    public ResponseWrapper<List<OrderDto>> ordersV22() {
        List<Order> orders = orderRepository.findAllWithMemberDeliveryOrderItemsItem();
        return orders.stream()
                .map(OrderDto::new)
                .collect(collectingAndThen(toList(), ResponseWrapper::new));
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String memberName;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            memberName = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

    //== DTO로 바로 조회 ==//

    @GetMapping("/api/v3.0/orders")
    public ResponseWrapper<List<OrderQueryDto>> ordersV30(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                          @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return new ResponseWrapper<>(orderQueryRepository.findOrderQueryDtos(offset, limit));
    }

    @GetMapping("/api/v3.1/orders")
    public ResponseWrapper<List<OrderQueryDto>> ordersV31(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                          @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return new ResponseWrapper<>(orderQueryRepository.findOrderQueryDtosOptim(offset, limit));
    }

    @GetMapping("/api/v3.2/orders")
    public ResponseWrapper<List<OrderQueryDto>> ordersV32() {
        return orderQueryRepository.findOrderQueryFlatDtos().stream()
                .collect(groupingBy(oqfd -> new OrderQueryDto(oqfd.getOrderId(), oqfd.getMemberName(), oqfd.getOrderDate(), oqfd.getOrderStatus(), oqfd.getAddress()),
                        mapping(oqfd -> new OrderItemQueryDto(oqfd.getOrderId(), oqfd.getItemName(), oqfd.getOrderPrice(), oqfd.getCount()), toList())))
                .entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getMemberName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(collectingAndThen(toList(), ResponseWrapper::new));
    }
}
