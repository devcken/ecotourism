package com.kakaopay.ecotourism.region;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Region {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    private String name;
}
