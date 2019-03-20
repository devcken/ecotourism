package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.region.Region;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Program {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    private String name;

    private String theme;

    @ManyToOne(targetEntity=Region.class)
    @JoinColumn(name="region_id")
    private Region region;

    private String regionDetails;

    private String intro;

    private String details;
}
