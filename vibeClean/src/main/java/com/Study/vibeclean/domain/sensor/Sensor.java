package com.Study.vibeclean.domain.sensor;

import jakarta.persistence.*;

@Entity
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name="x")
    private int x;

    @Column(nullable = false, name="y")
    private int y;

    @Column(nullable = false, name="z")
    private int z;

    protected Sensor(){} // jpa를 사용하는 경우 이렇게 entity 객체에 기본적인 protected 생성자를 사용해야 한다.

    public Sensor(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
