package com.kakaopay.ecotourism.region;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ecotourism/regions")
@RequiredArgsConstructor
public class RegionController {
    @NonNull private final RegionService regionService;

    @GetMapping()
    public ResponseEntity<List> regions() {
        return ResponseEntity.ok(regionService.regions());
    }
}
