package com.Study.vibeclean.domain.manual;

import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
public class ManualPower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name="power")
    private String power;

    public ManualPower(String power) {
        this.power = power;
    }
}
