package com.kakaopay.ecotourism.region;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findByName(String name);

    Optional<Region> findTop1ByNameContainingOrderByName(String regionLike);
}
