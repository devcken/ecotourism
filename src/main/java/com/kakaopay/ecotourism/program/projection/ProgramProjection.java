package com.kakaopay.ecotourism.program.projection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kakaopay.ecotourism.region.projection.RegionProjection;
import lombok.Data;

@Data public class ProgramProjection {
    @JsonIgnore
    private int id;
    private String name;
    private String theme;
    @JsonIgnore
    private RegionProjection region;
    @JsonIgnore
    private String regionDetails;
    @JsonIgnore
    private String intro;
    @JsonIgnore
    private String details;
}
