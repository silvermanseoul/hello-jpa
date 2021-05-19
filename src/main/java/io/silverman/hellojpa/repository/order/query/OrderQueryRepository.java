package io.silverman.hellojpa.repository.order.query;

import io.silverman.hellojpa.dto.order.query.OrderItemQueryDto;
import io.silverman.hellojpa.dto.order.query.OrderQueryDto;
import io.silverman.hellojpa.dto.order.query.OrderQueryFlatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(int offset, int limit) {
        // ToOne 관계의 엔티티만 JOIN
        List<OrderQueryDto> orderQueryDtos = findOrdersWithMemberDelivery(offset, limit);

        // ToMany 관계의 엔티티는 별도로 조회해서 조립
        orderQueryDtos.forEach(oqd -> oqd.setOrderItems(findOrderItemsWithItem(oqd.getOrderId())));

        return orderQueryDtos;
    }

    public List<OrderQueryDto> findOrderQueryDtosOptim(int offset, int limit) {
        // ToOne 관계의 엔티티만 JOIN
        List<OrderQueryDto> orderQueryDtos = findOrdersWithMemberDelivery(offset, limit);

        // ToMany 관계의 엔티티는 별도로 조회해서 조립
        List<Long> orderIds = orderQueryDtos.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(toList());
        Map<Long, List<OrderItemQueryDto>> orderItemQueryDtosMap = findOrderItemsMapWithItem(orderIds);
        orderQueryDtos.forEach(oqd -> oqd.setOrderItems(orderItemQueryDtosMap.get(oqd.getOrderId())));

        return orderQueryDtos;
    }

    public List<OrderQueryFlatDto> findOrderQueryFlatDtos() {
        // 모든 엔티티를 JOIN
        return em.createQuery(
                "select new io.silverman.hellojpa.dto.order.query.OrderQueryFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderQueryFlatDto.class)
                .getResultList();
    }

    private List<OrderQueryDto> findOrdersWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select new io.silverman.hellojpa.dto.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
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

    private Map<Long, List<OrderItemQueryDto>> findOrderItemsMapWithItem(List<Long> orderIds) {
        return em.createQuery(
                "select new io.silverman.hellojpa.dto.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultStream()
                .collect(groupingBy(OrderItemQueryDto::getOrderId));
    }
}
