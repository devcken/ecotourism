package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.region.Region;
import com.kakaopay.ecotourism.region.RegionService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProgramService {
    @NonNull private final ProgramRepository programRepository;

    @NonNull private final RegionService regionService;

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
            program.setRegion(regions.get(program.getRegionDetails().split(" ")[0]));
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
                val regionDetails = programData[3];
                val regionName = regionDetails.split(" ")[0];

                programs.add(programData);

                if (!regions.containsKey(regionName)) {
                    val region = new Region();
                    region.setName(regionName);

                    regions.put(regionName, region);
                }
            }
        }

        return Pair.of(programs, regions.values());
    }
}
