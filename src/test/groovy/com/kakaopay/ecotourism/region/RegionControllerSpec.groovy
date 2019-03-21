package com.kakaopay.ecotourism.region

import com.kakaopay.ecotourism.spec.ApiDocumentationSpec
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RegionControllerSpec extends ApiDocumentationSpec {
    def 'retrieve all of regions'() {
        expect:
        mockMvc.perform(get('/ecotourism/regions')
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                    document(
                            'retrieving-regions',
                            preprocessRequest(modifyingUri, prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath('[].id').type(JsonFieldType.NUMBER).optional().description(''),
                                    fieldWithPath('[].name').type(JsonFieldType.STRING).optional().description('')
                            )
                    )
                )
    }
}
