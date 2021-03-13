package io.silverman.hellojpa.service;

import io.silverman.hellojpa.domain.item.Book;
import io.silverman.hellojpa.domain.item.Item;
import io.silverman.hellojpa.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void 상품등록() throws Exception {
        // Given
        Item item = new Book();
        item.setName("JPA");

        // When
        Long savedId = itemService.saveItem(item);
        Item foundItem = itemRepository.findOne(savedId);

        // Then
        assertEquals(item, foundItem);
    }
}