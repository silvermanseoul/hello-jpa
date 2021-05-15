package io.silverman.hellojpa.repository.order.query;

import io.silverman.hellojpa.dto.order.query.OrderItemQueryDto;
import io.silverman.hellojpa.dto.order.query.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        // ToOne 관계의 엔티티만 JOIN
        List<OrderQueryDto> orderQueryDtos = findOrdersWithMemberDelivery();

        // ToMany 관계의 엔티티는 별도로 조회해서 조립
        orderQueryDtos.forEach(oqd -> oqd.setOrderItems(findOrderItemsWithItem(oqd.getOrderId())));

        return orderQueryDtos;
    }

    private List<OrderQueryDto> findOrdersWithMemberDelivery() {
        return em.createQuery(
                "select new io.silverman.hellojpa.dto.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItemsWithItem(Long orderId) {
        return em.createQuery(
                "select new io.silverman.hellojpa.dto.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
