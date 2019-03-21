package com.kakaopay.ecotourism.program;

import com.kakaopay.ecotourism.region.Region;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="programs")
@Data public class Program {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String name;

    @NotNull
    private String theme;

    @ManyToOne()
    @JoinColumn(name="region_id")
    private Region region;

    @NotNull
    private String regionDetails;

    private String intro;

    private String details;
}
