package com.kakaopay.ecotourism.spec

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.experimental.categories.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris

@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Category(ApiDocumentationSpec)
//@Transactional
class ApiDocumentationSpec extends Specification {
    @Autowired protected final ObjectMapper objectMapper
    @Autowired protected final MockMvc mockMvc

    final modifyingUri = modifyUris().host('ecotourism.kakaopay.com').port(5000)
}
