package com.kakaopay.ecotourism.program;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ecotourism/programs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProgramController {
    @NonNull private final ProgramService programService;

    @PostMapping()
    public ResponseEntity<Map> initialize() throws IOException {
        val response = new HashMap<String, Integer>();

        response.put("count", programService.initializePrograms());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/regions/{regionId}")
    public ResponseEntity<List<Program>> programs(@PathVariable Integer regionId) {
        return ResponseEntity.ok(null);
    }
}
