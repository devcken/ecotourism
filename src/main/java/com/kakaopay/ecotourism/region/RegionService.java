package com.kakaopay.ecotourism.region;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegionService {
    @NonNull private final RegionRepository regionRepository;

    public List<Region> regions() {
        return regionRepository.findAll();
    }

    public Optional<Region> region(final String name) {
        return regionRepository.findByName(name);
    }

    public Region save(final Region region) {
        return regionRepository.save(region);
    }

    public List<Region> saveAll(final Collection<Region> regions) {
        return regionRepository.saveAll(regions);
    }

    public void deleteAll() {
        regionRepository.deleteAll();
    }
}
