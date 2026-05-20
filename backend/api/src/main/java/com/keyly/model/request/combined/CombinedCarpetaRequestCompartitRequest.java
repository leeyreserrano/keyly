package com.keyly.model.request.combined;

import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.request.CompartitRequest;

public record CombinedCarpetaRequestCompartitRequest(
    CarpetaRequest carpetaRequest,
    CompartitRequest compartitRequest
) {

}
