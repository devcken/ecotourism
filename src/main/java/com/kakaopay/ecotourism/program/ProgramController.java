package com.kakaopay.ecotourism.program;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/ecotourism/programs")
@RequiredArgsConstructor
public class ProgramController {
    @NonNull private final ProgramService programService;

//    @PostMapping()
//    public ResponseEntity<Map> initialize() throws IOException {
//        val response = new HashMap<String, Integer>();
//
//        response.put("count", programService.initializePrograms());
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/regions/{regionId}")
    public ResponseEntity<List<ProgramProjection>> programs(@PathVariable final Integer regionId) {
        return ResponseEntity.ok(programService.programsByRegion(regionId));
    }

    @PostMapping()
    public ResponseEntity<Program> addProgram(@RequestBody @Valid final Program program) {
        val p = programService.add(program);
        return ResponseEntity.created(URI.create(format("/ecotourism/programs/%s", p.getId())))
            .body(p);
    }

    @PutMapping()
    public ResponseEntity<Program> modifyProgram(@RequestBody @Valid final Program program) {
        return programService.modify(program)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }
}
