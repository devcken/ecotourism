package com.kakaopay.ecotourism.program

import com.kakaopay.ecotourism.spec.ApiDocumentationSpec
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import spock.lang.Shared
import spock.lang.Stepwise

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Stepwise
class ProgramControllerSpec extends ApiDocumentationSpec {
    @Shared String accessToken

    def setup() {
        if (accessToken == null) {
            accessToken = issueAccessToken()
        }
    }

    def 'initialize all programs'() {
        expect:
        mockMvc.perform(post('/ecotourism/programs/regions')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").value(110)) // 110 is the number of data from data.csv
                .andDo(
                    document(
                            'initializing-programs',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath('count').type(JsonFieldType.NUMBER).description('')
                            )
                    )
                )
    }

    def 'retrieve programs based on given id of a certain region'() {
        given:
        final regionId = 1

        expect:
        mockMvc.perform(get('/ecotourism/programs/regions/{regionId}', regionId)
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                    document(
                            'programs-by-region-id',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath('[].id').type(JsonFieldType.NUMBER).description('').optional(),
                                    fieldWithPath('[].name').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('[].theme').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('[].region.id').type(JsonFieldType.NUMBER).description('').optional(),
                                    fieldWithPath('[].region.name').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('[].region_details').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('[].intro').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('[].details').type(JsonFieldType.STRING).description('').optional()
                            )
                    )
                )
    }

    def 'retrieving programs being held in a given region'() {
        given:
        final regionName = '서울'

        expect:
        mockMvc.perform(put('/ecotourism/programs/regions')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format('{"region": "%s"}', regionName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('region').isNumber())
                .andExpect(jsonPath('programs').isArray())
                .andDo(
                    document(
                            'programs-by-region-name',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath('region').type(JsonFieldType.NUMBER).description(''),
                                    fieldWithPath('programs.[].name').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('programs.[].theme').type(JsonFieldType.STRING).description('').optional(),
                            )
                    )
                )
    }

    def 'retrieving the count of programs per region by a given keyword'() {
        given:
        final keyword = '역사'

        expect:
        mockMvc.perform(post('/ecotourism/programs/numbers')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format('{"keyword": "%s"}', keyword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('keyword').value(keyword))
                .andExpect(jsonPath('programs').isArray())
                .andExpect(jsonPath('programs.[0].region').value('경상남도 통영'))
                .andExpect(jsonPath('programs.[0].count').value(3))
                .andDo(
                    document(
                            'count-of-programs-per-region-by-keyword',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath('keyword').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('programs').type(JsonFieldType.ARRAY).description(''),
                                    fieldWithPath('programs.[].region').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('programs.[].count').type(JsonFieldType.NUMBER).description('')
                            )
                    )
                )
    }

    def 'get term frequency for a given keyword'() {
        given:
        final keyword = '문화'

        expect:
        mockMvc.perform(post('/ecotourism/programs/tf')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format('{"keyword": "%s"}', keyword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('keyword').value(keyword))
                .andExpect(jsonPath('count').value(59))
                .andDo(
                    document(
                            'term-frequency-by-keyword',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath('keyword').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('count').type(JsonFieldType.NUMBER).description('')
                            )
                    )
                )
    }

    def 'recommend ecotourism program by region and keyword'() {
        when:
        final region1 = '평창'
        final keyword1 = '국립공원'

        then:
        mockMvc.perform(post('/ecotourism/programs/recommendation')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format('{"region": "%s", "keyword": "%s"}', region1, keyword1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('program').value(12))
                .andDo(
                    document(
                            'program-recommendation-by-region-and-keyword',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath('program').type(JsonFieldType.NUMBER).description('')
                            )
                    )
                )

        when:
        final region2 = '남해'
        final keyword2 = '생태체험'

        then:
        mockMvc.perform(post('/ecotourism/programs/recommendation')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format('{"region": "%s", "keyword": "%s"}', region2, keyword2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('program').value(22))
    }

    def 'add a new program'() {
        given:
        final program = new Program(name: 'NewYork Central Park', theme: 'Urban park, Public park', regionDetails: 'Manhattan NewYork City', intro: 'Central Park is an urban park in Manhattan, New York City. It is located between the Upper West Side and Upper East Side, roughly bounded by Fifth Avenue on the east, Central Park West (Eighth Avenue) on the west, Central Park South (59th Street) on the south, and Central Park North (110th Street) on the north. Central Park is the most visited urban park in the United States, with 40 million visitors in 2013, and one of the most filmed locations in the world. In terms of area, Central Park is the fifth largest park in New York City, covering 843 acres (341 ha).', details: 'Central Park was designed in 1858 by landscape architect and writer Frederick Law Olmsted and the English architect Calvert Vaux, who also designed Brooklyn\'s Prospect Park. Central Park has been a National Historic Landmark since 1962.' +
                '\n' +
                'Central Park is the fifth-largest park in New York City, behind Flushing Meadows-Corona Park, Van Cortlandt Park, the Staten Island Greenbelt, and Pelham Bay Park. Central Park is located on 843 acres (3.41 km2; 1.317 sq mi) of land, although its original area was 770 acres (3.1 km2). The park, with a perimeter of 6.1 miles (9.8 km), is bordered on the north by Central Park North (110th Street), on the south by Central Park South (59th Street), on the west by Central Park West (Eighth Avenue), and on the east by Fifth Avenue. It is 2.5 miles (4 km) long between Central Park South and Central Park North, and is 0.5 mile (0.8 km) wide between Fifth Avenue and Central Park West.\n' +
                '\n' +
                'Central Park constitutes its own United States census tract, number 143. According to American Community Survey 5-year estimates, the park\'s population in 2017 was four people, all female, with a median age of 19.8 years. However Central Park officials have rejected the claim of anyone permanently living there. The real estate value of Central Park was estimated by property appraisal firm Miller Samuel to be about $528.8 billion in December 2005.')

        expect:
        mockMvc.perform(post('/ecotourism/programs')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('name').value('NewYork Central Park'))
                .andExpect(jsonPath('theme').value('Urban park, Public park'))
                .andExpect(jsonPath('region.name').value('Manhattan NewYork'))
                .andExpect(jsonPath('region_details').value('Manhattan NewYork City'))
                .andExpect(jsonPath('intro').isNotEmpty())
                .andExpect(jsonPath('details').isNotEmpty())
                .andDo(
                    document(
                            'adding-program',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath('id').type(JsonFieldType.NUMBER).description('').ignored(),
                                    fieldWithPath('name').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('theme').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('region').type(JsonFieldType.OBJECT).description('').ignored(),
                                    fieldWithPath('region_details').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('intro').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('details').type(JsonFieldType.STRING).description('').optional()
                            ),
                            responseFields(
                                    fieldWithPath('id').type(JsonFieldType.NUMBER).description(''),
                                    fieldWithPath('name').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('theme').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('region.id').type(JsonFieldType.NUMBER).description(''),
                                    fieldWithPath('region.name').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('region_details').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('intro').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('details').type(JsonFieldType.STRING).description(''),
                            )
                    )
                )
    }

    def 'fail to add a new program(`theme` is not allowed as null)'() {
        given:
        final program = new Program(name: 'Failing program', regionDetails: 'region blah')

        expect:
        mockMvc.perform(post('/ecotourism/programs')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program)))
                .andExpect(status().isBadRequest())
    }

    def 'fail to add a new program(`regionDetails` is not allowed as null)'() {
        given:
        final program = new Program(name: 'Failing program', theme: 'theme blah')

        expect:
        mockMvc.perform(post('/ecotourism/programs')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program)))
                .andExpect(status().isBadRequest())
    }

    def 'modify a existing program'() {
        when:
        final program = new Program(name: 'Serengeti National', theme: 'National', regionDetails: 'Mara Tanzani', intro: 'The Serengeti National Park is a Tanzanian national park in the Serengeti ecosystem in the Mara and Simiyu regions.It is famous for its annual migration of over 1.5 million white-bearded (or brindled) wildebeest and 250,000 zebra and for its numerous Nile crocodile and honey badger', details: 'The Maasai people had been grazing their livestock in the open plains of eastern Mara Region, which they named "endless plains," for around 200 years when the first European explorer, Austrian Oscar Baumann, visited the area in 1892. The name "Serengeti" is an approximation of the word used by the Maasai to describe the area, siringet, which means "the place where the land runs on forever".\n' +
                '\n' +
                'The first American to enter the Serengeti, Stewart Edward White, recorded his explorations in the northern Serengeti in 1913. He returned to the Serengeti in the 1920s and camped in the area around Seronera for three months. During this time, he and his companions shot 50 lions')

        then:
        final result = mockMvc.perform(post('/ecotourism/programs')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('name').value('Serengeti National'))
                .andExpect(jsonPath('theme').value('National'))
                .andExpect(jsonPath('region.name').value('Mara Tanzani'))
                .andExpect(jsonPath('region_details').value('Mara Tanzani'))
                .andExpect(jsonPath('intro').isNotEmpty())
                .andExpect(jsonPath('details').isNotEmpty())
                .andReturn()

        when:
        final addedProgram = objectMapper.readValue(result.getResponse().getContentAsByteArray(), Program.class)

        addedProgram.name = addedProgram.name + ' Park'
        addedProgram.theme = addedProgram.theme + ' Park'
        addedProgram.regionDetails = addedProgram.regionDetails + 'a'
        addedProgram.intro = addedProgram.intro + '.'
        addedProgram.details = addedProgram.details + '.'

        then:
        mockMvc.perform(put('/ecotourism/programs')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addedProgram)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('name').value('Serengeti National Park'))
                .andExpect(jsonPath('theme').value('National Park'))
                .andExpect(jsonPath('region.name').value('Mara Tanzania'))
                .andExpect(jsonPath('region_details').value('Mara Tanzania'))
                .andExpect(jsonPath('intro').isNotEmpty())
                .andExpect(jsonPath('details').isNotEmpty())
                .andDo(
                    document(
                            'modifying-program',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath('id').type(JsonFieldType.NUMBER).description(''),
                                    fieldWithPath('name').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('theme').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('region').type(JsonFieldType.OBJECT).description('').ignored(),
                                    fieldWithPath('region.id').type(JsonFieldType.OBJECT).description('').ignored(),
                                    fieldWithPath('region.name').type(JsonFieldType.OBJECT).description('').ignored(),
                                    fieldWithPath('region_details').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('intro').type(JsonFieldType.STRING).description('').optional(),
                                    fieldWithPath('details').type(JsonFieldType.STRING).description('').optional()
                            ),
                            responseFields(
                                    fieldWithPath('id').type(JsonFieldType.NUMBER).description(''),
                                    fieldWithPath('name').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('theme').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('region.id').type(JsonFieldType.NUMBER).description(''),
                                    fieldWithPath('region.name').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('region_details').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('intro').type(JsonFieldType.STRING).description(''),
                                    fieldWithPath('details').type(JsonFieldType.STRING).description(''),
                            )
                    )
                )
    }

    def 'fail to modify program(`id` is required)'() {
        given:
        final program = new Program(name: 'Failing program', theme: 'theme blah', regionDetails: 'region blah')

        expect:
        mockMvc.perform(put('/ecotourism/programs')
                .header('Authorization', String.format('Bearer %s', accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program)))
                .andExpect(status().isBadRequest())
    }
}
