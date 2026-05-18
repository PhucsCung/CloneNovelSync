package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InventoryAiService;
import com.mycompany.myapp.service.dto.PredictResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class InventoryAiResource {

    private final Logger log = LoggerFactory.getLogger(InventoryAiResource.class);
    private final InventoryAiService inventoryAiService;

    public InventoryAiResource(InventoryAiService inventoryAiService) {
        this.inventoryAiService = inventoryAiService;
    }

    /**
     * API: GET /api/ai/predict/{bookId}
     */
    @GetMapping("/ai/predict/{bookId}")
    public ResponseEntity<PredictResponseDTO> predictRealtime(@PathVariable Long bookId) {
        log.debug("REST request để gọi AI dự báo cho sách: {}", bookId);
        Optional<PredictResponseDTO> result = inventoryAiService.predictSingleBook(bookId);
        return ResponseUtil.wrapOrNotFound(result);
    }
}
