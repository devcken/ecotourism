package com.kakaopay.ecotourism.program

import com.kakaopay.ecotourism.spec.ApiDocumentationSpec
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProgramControllerSpec extends ApiDocumentationSpec {
    def 'initialize all programs'() {
        expect:
        mockMvc.perform(post('/ecotourism/programs')
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
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                    document(
                            'retrieving-programs',
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

    def 'add a new program'() {
        given:
        final program = new Program()

        expect:
        mockMvc.perform(post('/ecotourism/programs')
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program)))
                .andExpect(status().isCreated())
                .andDo(
                    document(
                            'adding-program',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
//                            requestParameters(
//                                    parameterWithName('name'),
//                                    parameterWithName('theme'),
//                                    parameterWithName('region'),
//                                    parameterWithName('intro'),
//                                    parameterWithName('details')
//                            ),
                            responseFields(
                                    fieldWithPath('id').type(JsonFieldType.NUMBER),
                                    fieldWithPath('name').type(JsonFieldType.STRING),
                                    fieldWithPath('theme').type(JsonFieldType.STRING),
                                    fieldWithPath('region.name').type(JsonFieldType.STRING),
                                    fieldWithPath('region_details').type(JsonFieldType.STRING),
                                    fieldWithPath('intro').type(JsonFieldType.STRING),
                                    fieldWithPath('details').type(JsonFieldType.STRING),
                            )
                    )
                )
    }

    def 'modify a existing program'() {
        given:
        final program = new Program()

        expect:
        mockMvc.perform(put('/ecotourism/programs')
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program)))
                .andExpect(status().isOk())
                .andDo(
                    document(
                            'modifying-program',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
//                            requestParameters(
//                                    parameterWithName('name'),
//                                    parameterWithName('theme'),
//                                    parameterWithName('region'),
//                                    parameterWithName('intro'),
//                                    parameterWithName('details')
//                            ),
                            responseFields(
                                    fieldWithPath('id').type(JsonFieldType.NUMBER),
                                    fieldWithPath('name').type(JsonFieldType.STRING),
                                    fieldWithPath('theme').type(JsonFieldType.STRING),
                                    fieldWithPath('region.name').type(JsonFieldType.STRING),
                                    fieldWithPath('region_details').type(JsonFieldType.STRING),
                                    fieldWithPath('intro').type(JsonFieldType.STRING),
                                    fieldWithPath('details').type(JsonFieldType.STRING),
                            )
                    )
                )
    }
}
