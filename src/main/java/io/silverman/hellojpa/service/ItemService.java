package io.silverman.hellojpa.service;

import io.silverman.hellojpa.domain.item.Item;
import io.silverman.hellojpa.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Long saveItem(Item item) {
        itemRepository.save(item);
        return item.getId();
    }

    // 사용자가 price와 stockQuantity만 변경 가능하다고 가정
    // 변경 가능한 필드만 파라미터로 받음
    // 개수가 많으면 DTO 사용
    public void updateItem(Long id, int price, int stockQuantity) {
        Item foundItem = itemRepository.findOne(id);

        // Dirty checking
        foundItem.setPrice(price);
        foundItem.setStockQuantity(stockQuantity);
    }

    @Transactional(readOnly = true)
    public Item findItem(Long id) {
        return itemRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<Item> findItems() {
        return itemRepository.findAll();
    }
}
