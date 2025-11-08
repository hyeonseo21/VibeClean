package com.Study.vibeclean.domain.manual;

import jakarta.persistence.*;

@Entity
public class ManualSpeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name="speed")
    private int speed;

    public ManualSpeed(int speed) {
        this.speed = speed;
    }
}
