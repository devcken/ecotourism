package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.region.RegionProjection;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kakaopay.ecotourism.program.QProgram.program;
import static com.kakaopay.ecotourism.region.QRegion.region;
import static com.querydsl.core.types.Projections.bean;

@RequiredArgsConstructor
public class ProgramRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public List<ProgramProjection> findByRegion(Integer regionId) {
        return queryFactory
            .select(
                bean(ProgramProjection.class,
                    program.id,
                    program.name,
                    program.theme,
                    bean(RegionProjection.class,
                        region.id,
                        region.name
                    ).as("region"),
                    program.regionDetails,
                    program.intro,
                    program.details
                )
            )
            .from(program)
            .innerJoin(region)
            .fetchJoin()
            .on(program.region.id.eq(region.id))
            .where(region.id.eq(regionId))
            .fetch();
    }
}
