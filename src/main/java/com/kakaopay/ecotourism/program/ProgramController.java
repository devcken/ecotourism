package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.program.projection.ProgramProjection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@RestController
@RequestMapping("/ecotourism/programs")
@RequiredArgsConstructor
public class ProgramController {
    @NonNull private final ProgramService programService;

    @PostMapping("/regions")
    public ResponseEntity<Map> initialize() throws IOException {
        val response = new HashMap<String, Integer>();

        response.put("count", programService.initializePrograms());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/regions/{regionId}")
    public ResponseEntity<List<ProgramProjection>> programs(@PathVariable final Integer regionId) {
        return ResponseEntity.ok(programService.programsByRegion(regionId));
    }

    @PutMapping("/regions")
    public ResponseEntity programs(@RequestBody final Map<String, Object> params) { // Using @PathVariable it will be more RESTful.
        if (!params.containsKey("region")) {
            return ResponseEntity.badRequest().build();
        }

        val regionAndPrograms = programService.programsByRegion(params.get("region").toString());

        return regionAndPrograms.getFirst()
            .map(r -> {
                val response = new HashMap<String, Object>();

                response.put("region", r.getId());
                response.put("programs", regionAndPrograms.getSecond());

                return ResponseEntity.ok(response);
            }).orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/numbers")
    public ResponseEntity numberOfProgramsByKeyword(@RequestBody final Map<String, Object> params) { // Using @PathVariable it will be more RESTful.
        if (!params.containsKey("keyword")) {
            return ResponseEntity.badRequest().build();
        }

        val numberOfPrograms = programService.numberOfPrograms(params.get("keyword").toString());

        params.put("programs", numberOfPrograms);

        return ResponseEntity.ok(params);
    }

    @PostMapping("/tf")
    public ResponseEntity termFrequencyByKeyword(@RequestBody final Map<String, Object> params) {
        if (!params.containsKey("keyword")) {
            return ResponseEntity.badRequest().build();
        }

        val termFrequency = programService.termFrequency(params.get("keyword").toString());

        params.put("count", termFrequency);

        return ResponseEntity.ok(params);
    }

    @PostMapping("/recommendation")
    public ResponseEntity recommendProgramByKeyword(@RequestBody final Map<String, Object> params) {
        if (!params.containsKey("region") || !params.containsKey("keyword")) {
            return ResponseEntity.badRequest().build();
        }

        val region = params.get("region").toString();
        val keyword = params.get("keyword").toString();

        params.clear();

        return programService.findRecommendedProgram(region, keyword)
            .map(p -> {
                params.put("program", p.getId());
                return ResponseEntity.ok(params);
            })
            .orElse(ResponseEntity.noContent().build());
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
