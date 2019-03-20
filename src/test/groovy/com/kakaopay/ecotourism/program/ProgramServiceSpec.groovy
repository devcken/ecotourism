package com.kakaopay.ecotourism.program


import spock.lang.Specification

class ProgramServiceSpec extends Specification {
    final ecotourismRepository = Mock(ProgramRepository)
    final ecotourismService = new ProgramService(ecotourismRepository)

    
}
