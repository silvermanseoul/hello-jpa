package io.silverman.hellojpa.domain.item;

import io.silverman.hellojpa.domain.Category;
import io.silverman.hellojpa.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    private int price;
    private int stockQuantity;

    public void addStock(int quantity) {
        stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고 수량이 부족합니다.");
        }
        stockQuantity = restStock;
    }
}
