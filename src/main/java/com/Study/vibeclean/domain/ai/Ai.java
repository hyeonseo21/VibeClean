package com.Study.vibeclean.domain.ai;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="ai")
public class Ai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name="power") // 여기도 이제 아래로 다 double로 바꿔주었다. 내가 이 센서 값들을 그렇게 바꾸었기 때문
    private String power;

    @Column(nullable = false, name="current_floor")
    private String currentFloor;

    @Column(nullable = false, name="fan_speed")
    private int fan_speed;

    @Column(nullable = false,name="time") // DB 수정하면서 이 부분이 추가되었다.
    private LocalDateTime time;

    @Column (nullable = false, name="mode")
    private String mode;

    @Column (nullable = false, name="x")

    private float x;

    @Column (nullable = false, name="y")
    private float y;

    @Column (nullable = false, name="z")
    private float z;

    protected Ai(){} // jpa를 사용하는 경우 이렇게 entity 객체에 기본적인 protected 생성자를 사용해야 한다.

    public Ai(String power, String currnet_floor, int fan_speed, LocalDateTime time, String mode, float x, float y, float z) {
        this.power = power;
        this.currentFloor = currnet_floor;
        this.fan_speed = fan_speed;
        this.time = time;
        this.mode = mode;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getPower() {
        return power;
    }

    public String getCurrent_floor() {
        return currentFloor;
    }

    public int getFan_speed() {
        return fan_speed;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getMode() {
        return mode;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
