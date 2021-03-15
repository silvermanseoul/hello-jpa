package io.silverman.hellojpa.service;

import io.silverman.hellojpa.domain.*;
import io.silverman.hellojpa.domain.item.Item;
import io.silverman.hellojpa.repository.ItemRepository;
import io.silverman.hellojpa.repository.MemberRepository;
import io.silverman.hellojpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Long order(Long memberId, Long itemId, int count) {
        // Member 엔티티 조회
        Member member = memberRepository.findOne(memberId);

        // Item 엔티티 조회
        Item item = itemRepository.findOne(itemId);

        // Delivery 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // OrderItem 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // Order 생성 후 저장
        Order order = Order.createOrder(member, delivery, orderItem);
        orderRepository.save(order); // Delivery와 OrderItem은 cascade

        return order.getId();
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel(); // Dirty checking
    }

    @Transactional(readOnly = true)
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
