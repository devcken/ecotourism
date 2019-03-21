package com.kakaopay.ecotourism.region;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="regions")
@Data public class Region {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String name;
}
