package com.keyly.model.response;

import com.keyly.model.PwnedHash;

public record PawnedPasswordResponse(
        String hash,
        String count) {

    public PawnedPasswordResponse(PwnedHash hash) {
        this(
                hash.getKey().getPrefix() + hash.getKey().getSha1(),
                String.valueOf(hash.getCount()));
    }

}
