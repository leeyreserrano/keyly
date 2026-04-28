package com.keyly.model.request.combined;

import com.keyly.model.request.CompartitRequest;
import com.keyly.model.request.ItemRequest;

public record CombinedItemRequestCompartitRequest(
    ItemRequest itemRequest,
    CompartitRequest compartitRequest
) {

}
