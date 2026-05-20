package com.example.keyly_projecte_intermodular.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompartitItemRequest {
    private ItemRequest itemRequest;
    private CompartitRequest compartitRequest;
}
