package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.region.RegionProjection;
import lombok.Data;

@Data public class ProgramProjection {
    private int id;
    private String name;
    private String theme;
    private RegionProjection region;
    private String regionDetails;
    private String intro;
    private String details;
}
