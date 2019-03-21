package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.program.projection.NumOfProgramProjection;
import com.kakaopay.ecotourism.program.projection.ProgramProjection;

import java.util.List;

public interface ProgramRepositoryCustom {
    List<ProgramProjection> findByRegion(Integer regionId);

    List<NumOfProgramProjection> countByIntro(String keyword);

    List<ProgramProjection> findByDetailsContaining(String keyword);
}
