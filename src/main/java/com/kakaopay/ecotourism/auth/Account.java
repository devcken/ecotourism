package com.kakaopay.ecotourism.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account")
@Data public class Account {
    @Id
    private String username;

    @JsonIgnore
    private String password;

    private String authorities = "USER";
}
