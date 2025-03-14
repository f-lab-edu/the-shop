package com.flab.theshop.service;

import com.flab.theshop.domain.Item;
import com.flab.theshop.exception.item.ItemException;
import com.flab.theshop.respository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(ItemException.ITEM_NOT_FOUND::get);
    }

    @Transactional
    public void remove(Long itemId) {
        Item item = findItemById(itemId);
        itemRepository.delete(item);
    }
}
