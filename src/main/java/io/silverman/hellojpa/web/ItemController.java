package io.silverman.hellojpa.web;

import io.silverman.hellojpa.domain.item.Book;
import io.silverman.hellojpa.domain.item.Item;
import io.silverman.hellojpa.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String itemsNewForm(Model model) {
        model.addAttribute("bookForm", new BookForm());

        return "item/item-create-form";
    }

    @PostMapping("/items/new")
    public String itemsNew(BookForm bookForm) {
        Book book = new Book();
        book.setName(bookForm.getName());
        book.setPrice(bookForm.getPrice());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setAuthor(bookForm.getAuthor());
        book.setIsbn(bookForm.getIsbn());

        itemService.saveItem(book);

        return "redirect:/items";
    }

    @GetMapping("/items")
    public String items(Model model) {
        List<Item> items = itemService.findItems();

        model.addAttribute("items", items);

        return "item/item-list";
    }

    @GetMapping("/items/{itemId}/edit")
    public String itemsEditForm(@PathVariable("itemId") Long itemId, Model model) {
        Book book = (Book) itemService.findItem(itemId);

        BookForm bookForm = new BookForm();
        bookForm.setId(book.getId());
        bookForm.setName(book.getName());
        bookForm.setPrice((book.getPrice()));
        bookForm.setStockQuantity(book.getStockQuantity());
        bookForm.setAuthor(book.getAuthor());
        bookForm.setIsbn(book.getIsbn());

        model.addAttribute("bookForm", bookForm);

        return "item/item-update-form";
    }

    @PostMapping("items/{itemId}/edit")
    public String itemsEdit(@PathVariable Long itemId, @ModelAttribute("bookForm") BookForm bookForm) {
        itemService.updateItem(itemId, bookForm.getPrice(), bookForm.getStockQuantity());

        return "redirect:/items";
    }
}
