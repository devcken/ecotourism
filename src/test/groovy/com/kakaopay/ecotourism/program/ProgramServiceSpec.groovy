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
        final programs = [
                new ProgramProjection(theme: '아동·청소년 체험학습', intro: '1일차: 어름치마을 인근 탐방, 2일차: 오대산국립공원 탐방, 3일차: 봉평마을 탐방', details: ' 1일차: 백룡동굴, 민물고기생태관 체험, 칠족령 트레킹\n' +
                        ' 2일차: 대관령 양떼목장, 신재생 에너지전시관, 오대산국립공원\n' +
                        ' 3일차: 이효석 문학관, 봉평마을'),
                new ProgramProjection(theme: '숲 치유,', intro: '선재길, 한국자생식물원, 전나무숲, 월정사, 방아다리약수', details: ' 천년의 숲으로 불리는 오대산 전나무숲과 선재길에서 다양한 숲치유 프로그램 체험'),
                new ProgramProjection(theme: '자연생태,', intro: '소금강, 삼산테마파크', details: ' 오대산국립공원의 대표 경관인 소금강지구에서 대자연의 아름다움을 제대로 즐긴다.'),
                new ProgramProjection(theme: '문화생태체험,자연생태체험,', intro: '오대산국립공원 전나무숲, 월정사, 상원사, 신재생에너지전시관, 대관령 양떼목장, 한국자생식물원, 허브나라', details: ' - 전나무숲 생태체험\n' +
                        ' - 월정사, 상원사 역사문화 체험\n' +
                        ' - 대관령 양떼목장 체험 \n' +
                        ' - 신재생에너지관 체험\n' +
                        ' - 한국자생식물원 체험\n' +
                        ' - 허브나라 체험')
        ]

        expect:
        programService.tfIdfs(programs, term) == tfIdfs

        where:
        term     | tfIdfs
        '국립공원' | [0.09995098928663995, 0.0, 0.03748162098248998, 0.062469368304149966]
        '생태체험' | [0.0, 0.0, 0.0, 0.42144199392957366]
    }

    def 'get recommended program'(String keyword, ProgramProjection program) {
        given:
        final region = 'region'

        regionService.regionLike(region) >> Optional.of(new Region(id: 1))
        programRepository.findByRegion(1) >> [
                new ProgramProjection(theme: '아동·청소년 체험학습', intro: '1일차: 어름치마을 인근 탐방, 2일차: 오대산국립공원 탐방, 3일차: 봉평마을 탐방', details: ' 1일차: 백룡동굴, 민물고기생태관 체험, 칠족령 트레킹\n' +
                        ' 2일차: 대관령 양떼목장, 신재생 에너지전시관, 오대산국립공원\n' +
                        ' 3일차: 이효석 문학관, 봉평마을'),
                new ProgramProjection(theme: '숲 치유,', intro: '선재길, 한국자생식물원, 전나무숲, 월정사, 방아다리약수', details: ' 천년의 숲으로 불리는 오대산 전나무숲과 선재길에서 다양한 숲치유 프로그램 체험'),
                new ProgramProjection(theme: '자연생태,', intro: '소금강, 삼산테마파크', details: ' 오대산국립공원의 대표 경관인 소금강지구에서 대자연의 아름다움을 제대로 즐긴다.'),
                new ProgramProjection(theme: '문화생태체험,자연생태체험,', intro: '오대산국립공원 전나무숲, 월정사, 상원사, 신재생에너지전시관, 대관령 양떼목장, 한국자생식물원, 허브나라', details: ' - 전나무숲 생태체험\n' +
                        ' - 월정사, 상원사 역사문화 체험\n' +
                        ' - 대관령 양떼목장 체험 \n' +
                        ' - 신재생에너지관 체험\n' +
                        ' - 한국자생식물원 체험\n' +
                        ' - 허브나라 체험')
        ]

        expect:
        programService.findRecommendedProgram(region, keyword)

        where:
        keyword  | program
        '국립공원' | new ProgramProjection(theme: '아동·청소년 체험학습', intro: '1일차: 어름치마을 인근 탐방, 2일차: 오대산국립공원 탐방, 3일차: 봉평마을 탐방', details: ' 1일차: 백룡동굴, 민물고기생태관 체험, 칠족령 트레킹\n' +
                ' 2일차: 대관령 양떼목장, 신재생 에너지전시관, 오대산국립공원\n' +
                ' 3일차: 이효석 문학관, 봉평마을')
        '생태체험' | new ProgramProjection(theme: '문화생태체험,자연생태체험,', intro: '오대산국립공원 전나무숲, 월정사, 상원사, 신재생에너지전시관, 대관령 양떼목장, 한국자생식물원, 허브나라', details: ' - 전나무숲 생태체험\n' +
                ' - 월정사, 상원사 역사문화 체험\n' +
                ' - 대관령 양떼목장 체험 \n' +
                ' - 신재생에너지관 체험\n' +
                ' - 한국자생식물원 체험\n' +
                ' - 허브나라 체험')
    }
}
