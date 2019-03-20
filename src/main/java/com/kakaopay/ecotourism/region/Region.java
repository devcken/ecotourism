package com.kakaopay.ecotourism.region;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="regions")
public class Region {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String name;
}
