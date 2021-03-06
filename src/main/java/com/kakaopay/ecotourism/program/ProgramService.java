package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.program.projection.NumOfProgramProjection;
import com.kakaopay.ecotourism.program.projection.ProgramProjection;
import com.kakaopay.ecotourism.region.Region;
import com.kakaopay.ecotourism.region.RegionService;
import com.opencsv.CSVReaderBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ProgramService {
    @NonNull private final ProgramRepository programRepository;

    @NonNull private final RegionService regionService;

    private final static double PROGRAM_THEME_WEIGHT = 0.2;
    private final static double PROGRAM_INTRO_WEIGHT = 0.5;
    private final static double PROGRAM_DETAILS_WEIGHT = 0.3;

    @Transactional
    int initializePrograms() throws IOException {
        programRepository.deleteAll();
        regionService.deleteAll();

        val fileData = readDataFromFile();

        val regions = regionService.saveAll(fileData.getSecond()).stream().collect(Collectors.toMap(Region::getName, r -> r));

        val programs = fileData.getFirst().stream().map(x -> {
            Program program = new Program();
            program.setName(x[1]);
            program.setTheme(x[2]);
            program.setRegionDetails(x[3]);
            program.setRegion(regions.get(extractRegionName(program.getRegionDetails())));
            program.setIntro(x[4]);
            program.setDetails(x[5]);
            return program;
        }).collect(Collectors.toList());

        return programRepository.saveAll(programs).size();
    }

    Pair<List<String[]>, Collection<Region>> readDataFromFile() throws IOException {
        val programs = new ArrayList<String[]>();
        val regions = new HashMap<String, Region>();

        try (val csvReader = new CSVReaderBuilder(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("data.csv")))).withSkipLines(1).build()) {
            String[] programData;

            while ((programData = csvReader.readNext()) != null) {
                programs.add(programData);

                val regionName = extractRegionName(programData[3]);

                if (!regions.containsKey(regionName)) {
                    val region = new Region();
                    region.setName(regionName);

                    regions.put(regionName, region);
                }
            }
        }

        return Pair.of(programs, regions.values());
    }

    List<ProgramProjection> programsByRegion(final Integer regionId) {
        return programRepository.findByRegion(regionId);
    }

    Pair<Optional<Region>, List<ProgramProjection>> programsByRegion(final String region) {
        return regionService.regionLike(region)
            .map(r -> Pair.of(Optional.of(r), programsByRegion(r.getId())))
            .orElse(Pair.of(Optional.empty(), new ArrayList<>()));
    }

    List<NumOfProgramProjection> numberOfPrograms(final String keyword) {
        return programRepository.countByIntro(keyword);
    }

    Integer termFrequency(final String keyword) {
        return programRepository.findByDetailsContaining(keyword)
            .stream()
            .map(p -> termFrequency(p.getDetails(), keyword, 0))
            .reduce(0, (x, y) -> x + y);
    }

    int termFrequency(final String document, final String term, final int tf) {
        return document.contains(term) ?
            termFrequency(document.substring(document.indexOf(term) + term.length()), term, tf + 1) : tf;
    }

    Optional<ProgramProjection> findRecommendedProgram(final String region, final String keyword) {
        val programs = programsByRegion(region);

        if (!programs.getFirst().isPresent() || programs.getSecond().size() == 0) {
            return Optional.empty();
        }

        val tfIdfs = tfIdfs(programs.getSecond(), keyword);

        val maxIndex = IntStream.range(0, tfIdfs.size())
            .boxed()
            .max(Comparator.comparing(tfIdfs::get))
            .orElse(-1);

        return maxIndex >= programs.getSecond().size() ? Optional.empty() : Optional.of(programs.getSecond().get(maxIndex));
    }

    List<Double> tfIdfs(final List<ProgramProjection> programs, final String term) {
        val idf = inverseDocumentFrequency(programs.stream()
            .map(p -> format("%s %s %s", p.getTheme(), p.getIntro(), p.getDetails()))
            .collect(Collectors.toList()), term);

        return programs.stream().map(p -> {
            val tf = termFrequency(p.getTheme(), term, 0) * PROGRAM_THEME_WEIGHT
                + termFrequency(p.getIntro(), term, 0) * PROGRAM_INTRO_WEIGHT
                + termFrequency(p.getDetails(), term, 0) * PROGRAM_DETAILS_WEIGHT;

            return tf * idf;
        }).collect(Collectors.toList());
    }

    double inverseDocumentFrequency(final List<String> documents, final String term) {
        val docSize = documents.size();
        val docCountForTerm = documents.stream().filter(d -> d.contains(term)).mapToDouble(d -> 1).sum();
        val df = docSize / docCountForTerm;

        return Math.log10(df);
    }

    @Transactional
    Program add(final Program program) {
        val region = saveOrGetRegion(program.getRegionDetails());

        program.setRegion(region);

        return programRepository.save(program);
    }

    @Transactional
    Optional<Program> modify(final Program program) {
        return programRepository.findById(program.getId())
            .map(p -> {
                if (!p.getRegionDetails().equals(program.getRegionDetails())) {
                    val region = saveOrGetRegion(program.getRegionDetails());

                    p.setRegion(region);
                    p.setRegionDetails(program.getRegionDetails());
                }

                p.setName(program.getName());
                p.setTheme(program.getTheme());
                p.setRegionDetails(program.getRegionDetails());
                p.setIntro(program.getIntro());
                p.setDetails(program.getDetails());

                return Optional.of(programRepository.save(p));
            }).orElse(Optional.empty());
    }

    private Region saveOrGetRegion(final String regionDetails) {
        val regionName = extractRegionName(regionDetails);
        return regionService.region(regionName)
            .orElseGet(() -> {
                val r = new Region();
                r.setName(regionName);
                return regionService.save(r);
            });
    }

    String extractRegionName(final String regionDetails) {
        val regionValues = regionDetails.split(" ");
        return regionValues.length >= 2 ?
            format("%s %s", regionValues[0], regionValues[1].trim().replace(",", "")) : regionValues[0];
    }
}
