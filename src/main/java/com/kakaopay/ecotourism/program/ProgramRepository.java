package com.kakaopay.ecotourism.program;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer>, ProgramRepositoryCustom {
}
