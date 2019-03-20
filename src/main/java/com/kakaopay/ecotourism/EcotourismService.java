package com.kakaopay.ecotourism;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EcotourismService {
    @NonNull private final EcotourismRepository ecotourismRepository;
}
