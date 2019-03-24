package com.kakaopay.ecotourism.spec

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.experimental.categories.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.util.LinkedMultiValueMap
import spock.lang.Specification

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Category(ApiDocumentationSpec)
class ApiDocumentationSpec extends Specification {
    @Autowired protected final ObjectMapper objectMapper
    @Autowired protected final MockMvc mockMvc

    final modifyingUri = modifyUris().host('ecotourism.kakaopay.com').port(5000)

    def issueAccessToken() {
        final params = new LinkedMultiValueMap()
        params.add('grant_type', 'password')
        params.add('client_id', 'client')
        params.add('username', 'user')
        params.add('password', 'password')

        final result = mockMvc.perform(post('/oauth/token')
                .params(params)
                .header('Authorization', 'Basic Y2xpZW50OnBhc3N3b3Jk')
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        final jsonParser = new JacksonJsonParser()

        return jsonParser.parseMap(result).get('access_token').toString()
    }
}
