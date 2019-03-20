package com.kakaopay.ecotourism

import spock.lang.Specification

class EcotourismServiceSpec extends Specification {
    final ecotourismRepository = Mock(EcotourismRepository)
    final ecotourismService = new EcotourismService(ecotourismRepository)

    
}
