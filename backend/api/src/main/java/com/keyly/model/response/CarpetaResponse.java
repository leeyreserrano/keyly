package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CarpetaResponse(
        UUID uuid,
        BagulResponse bagul,
        String nom,
        Boolean favorit,
        LocalDateTime dataCreacio,
        LocalDateTime dataEditat,
        LocalDateTime ultimAccess,
        Long comptadorAccess,
        List<ItemResponse> items) {

}
