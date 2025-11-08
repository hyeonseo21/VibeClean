package com.Study.vibeclean.domain.status;

import jakarta.persistence.*;

@Entity
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 255, name="power")
    private String power;

    @Column(nullable = false, length = 255, name="current_Floor")
    private String currentFloor;

    @Column(nullable = false, name="fan_Speed")
    private int fanSpeed;

    @Column(nullable = false, name="x")
    private int x;

    @Column(nullable = false, name="y")
    private int y;

    protected Status(){} //jpa를 사용하는 경우 entity객체에 이런 식으로 항상 기본 생성자를 작성해야 한다.

    public Status(String power, String currentFloor, int fanSpeed, int x, int y) {
        this.power = power;
        this.currentFloor = currentFloor;
        this.fanSpeed = fanSpeed;
        this.x = x;
        this.y = y;
    }

    public String getPower() {
        return power;
    }

    public String getCurrentFloor() {
        return currentFloor;
    }

    public int getFanSpeed() {
        return fanSpeed;
    }

}
