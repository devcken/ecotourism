package com.kakaopay.ecotourism.program;

import java.util.List;

public interface ProgramCustomRepository {
    List<ProgramProjection> findByRegion(Integer regionId);
}
