package io.silverman.hellojpa.web;

import io.silverman.hellojpa.domain.Member;
import io.silverman.hellojpa.domain.Order;
import io.silverman.hellojpa.domain.OrderSearch;
import io.silverman.hellojpa.domain.item.Item;
import io.silverman.hellojpa.service.ItemService;
import io.silverman.hellojpa.service.MemberService;
import io.silverman.hellojpa.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/orders/new")
    public String ordersNewForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/order-create-form";
    }

    @PostMapping("/orders/new")
    public String ordersNew(@RequestParam("memberId") Long memberId,
                            @RequestParam("itemId") Long itemId,
                            @RequestParam("count") int count) {
        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orders(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);

        model.addAttribute("orders", orders);

        return "order/order-list";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String ordersCancel(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);

        return "redirect:/orders";
    }

}
