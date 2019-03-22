package com.kakaopay.ecotourism.program

import com.kakaopay.ecotourism.program.projection.ProgramProjection
import com.kakaopay.ecotourism.region.Region
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
        data.second.size() == 47
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

    def 'term frequency'(String document, String term, int tf) {
        expect:
        programService.termFrequency(document, term, 0) == tf

        where:
        document                                            | term     | tf
        '대한민국은 민주공화국이다.'                               | '대한민국' | 1
        '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.' | '대한민국' | 1
        '대한민국의 국민이 되는 요건은 법률로 정한다.'                 | '대한민국' | 1
        '국가는 법률이 정하는 바에 의하여 재외국민을 보호할 의무를 진다.'  | '대한민국' | 0
        '대한민국은 민주공화국이다.'                               | '국민' | 0
        '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.' | '국민' | 2
        '대한민국의 국민이 되는 요건은 법률로 정한다.'                 | '국민' | 1
        '국가는 법률이 정하는 바에 의하여 재외국민을 보호할 의무를 진다.'  | '국민' | 1
    }

    def 'inverse document frequency'(String term, double idf) {
        given:
        final documents = ['대한민국은 민주공화국이다.', '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.',
                           '대한민국의 국민이 되는 요건은 법률로 정한다.', '국가는 법률이 정하는 바에 의하여 재외국민을 보호할 의무를 진다.']

        expect:
        programService.inverseDocumentFrequency(documents, term) == idf

        where:
        term     | idf
        '대한민국' | 0.12493873660829993
        '국민'    | 0.12493873660829993
    }

    def 'tf-idf'(String term, List tfIdfs) {
        given:
        final documents = ['대한민국은 민주공화국이다.', '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.',
                           '대한민국의 국민이 되는 요건은 법률로 정한다.', '국가는 법률이 정하는 바에 의하여 재외국민을 보호할 의무를 진다.']

        expect:
        programService.tfIdfs(documents, term) == tfIdfs

        where:
        term     | tfIdfs
        '대한민국' | [0.12493873660829993, 0.12493873660829993, 0.12493873660829993, 0.0]
        '국민'    | [0.0, 0.24987747321659987, 0.12493873660829993, 0.12493873660829993]
    }

    def 'get recommended program'(String keyword, ProgramProjection program) {
        given:
        final region = 'region'

        regionService.regionLike(region) >> Optional.of(new Region(id: 1))
        programRepository.findByRegion(1) >> [
                new ProgramProjection(theme: '', intro: '대한민국은 민주공화국이다.', details: ''),
                new ProgramProjection(theme: '', intro: '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.', details: ''),
                new ProgramProjection(theme: '', intro: '대한민국의 국민이 되는 요건은 법률로 정한다.', details: ''),
                new ProgramProjection(theme: '', intro: '국가는 법률이 정하는 바에 의하여 재외국민을 보호할 의무를 진다.', details: '')
        ]

        expect:
        programService.findRecommendedProgram(region, keyword)

        where:
        keyword  | program
        '대한민국' | new ProgramProjection(theme: '', intro: '대한민국은 민주공화국이다.', details: '')
        '국민'    | new ProgramProjection(theme: '', intro: '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.', details: '')
    }
}
