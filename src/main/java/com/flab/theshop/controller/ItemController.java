package com.flab.theshop.controller;

import com.flab.theshop.dto.item.ItemRequest;
import com.flab.theshop.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.flab.theshop.exception.SuccessMessage.ITEM_CREATE_OK;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Response<Void> newItem(@Valid @RequestBody ItemRequest request) {
        itemService.save(request.toEntity());
        return Response.success(ITEM_CREATE_OK.getMessage());
    }
}
