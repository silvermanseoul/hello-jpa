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

    @Transactional(readOnly = true)
    public Item findItem(Long id) {
        return itemRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<Item> findItems() {
        return itemRepository.findAll();
    }
}
