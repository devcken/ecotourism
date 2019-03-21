package com.kakaopay.ecotourism.program

import com.kakaopay.ecotourism.region.RegionService
import spock.lang.Specification

class ProgramServiceSpec extends Specification {
    final programRepository = Mock(ProgramRepositoryCustom)
    final regionService = Mock(RegionService)
    final programService = new ProgramService(programRepository, regionService)

    def 'read program data from a given file'() {
        given:
        final data = programService.readDataFromFile()

        expect:
        data.first.size() == 110
        data.second.size() == 10
    }

    def 'extracting region name from detail of regions'(String regionDetails, String regionName) {
        expect:
        programService.extractRegionName(regionDetails) == regionName

        where:
        regionDetails  | regionName
        "a"            | "a"
        "a b"          | "a b"
        "a b c"        | "a b"
    }
}
