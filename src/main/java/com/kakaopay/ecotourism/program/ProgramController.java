package com.kakaopay.ecotourism.program;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.plugin.util.ProgressMonitorAdapter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ecotourism/programs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProgramController {
    @NonNull private final ProgramService programService;

    @PostMapping()
    public ResponseEntity<?> initialize() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/regions/{regionId}")
    public ResponseEntity<List<Program>> programs(@PathVariable Integer regionId) {
        return ResponseEntity.ok(null);
    }
}
