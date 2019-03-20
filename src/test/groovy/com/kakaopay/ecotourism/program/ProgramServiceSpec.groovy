package com.kakaopay.ecotourism.program

import com.kakaopay.ecotourism.region.RegionService
import spock.lang.Specification

class ProgramServiceSpec extends Specification {
    final programRepository = Mock(ProgramRepository)
    final regionService = Mock(RegionService)
    final programService = new ProgramService(programRepository, regionService)

    def 'read program data from a given file'() {
        given:
        final data = programService.readDataFromFile()

        expect:
        data.first.size() == 110
        data.second.size() == 10
    }
}
