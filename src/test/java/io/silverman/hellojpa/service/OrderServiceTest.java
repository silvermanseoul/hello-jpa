package io.silverman.hellojpa.service;

import io.silverman.hellojpa.domain.Address;
import io.silverman.hellojpa.domain.Member;
import io.silverman.hellojpa.domain.Order;
import io.silverman.hellojpa.domain.OrderStatus;
import io.silverman.hellojpa.domain.item.Book;
import io.silverman.hellojpa.domain.item.Item;
import io.silverman.hellojpa.exception.NotEnoughStockException;
import io.silverman.hellojpa.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 주문() throws Exception {
        // Given
        Member member = createMember("Park", "서울", "청룡로", "11111");

        int price = 10000;
        int stockQuantity = 10;
        Item item = createItem("JPA", price, stockQuantity);

        int orderCount = 2;

        // When
        Long savedId = orderService.order(member.getId(), item.getId(), orderCount);
        Order foundOrder = orderRepository.findOne(savedId);

        // Then
        assertEquals(OrderStatus.ORDER, foundOrder.getStatus(), "주문 시 상태는 ORDER");
        assertEquals(1, foundOrder.getOrderItems().size());
        assertEquals(price * orderCount, foundOrder.getTotalPrice());
        assertEquals(stockQuantity - orderCount, item.getStockQuantity());
    }

    @Test
    public void 재고수량부족() throws Exception {
        // Given
        Member member = createMember("Park", "서울", "청룡로", "11111");

        int price = 10000;
        int stockQuantity = 10;
        Item item = createItem("JPA", price, stockQuantity);

        int orderCount = 11;

        // When
        NotEnoughStockException e = assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), orderCount));

        // Then
        assertEquals("재고 수량이 부족합니다.", e.getMessage());
    }

    @Test
    public void 주문취소() throws Exception {
        // Given
        Member member = createMember("Park", "서울", "청룡로", "11111");

        int price = 10000;
        int stockQuantity = 10;
        Item item = createItem("JPA", price, stockQuantity);

        int orderCount = 2;
        Long savedId = orderService.order(member.getId(), item.getId(), orderCount);

        // When
        orderService.cancelOrder(savedId);
        Order foundOrder = orderRepository.findOne(savedId);

        // Then
        assertEquals(OrderStatus.CANCEL, foundOrder.getStatus(), "주문 취소 시 상태는 CANCEL");
        assertEquals(stockQuantity, item.getStockQuantity());
    }

    private Member createMember(String name, String city, String street, String zipcode) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        em.persist(member);
        return member;
    }

    private Item createItem(String name, int price, int stockQuantity) {
        Item item = new Book();
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        em.persist(item);
        return item;
    }
}