package com.Study.vibeclean.domain.auto2d;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="auto_2d")
public class Auto2D {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name="x") // 여기도 이제 아래로 다 double로 바꿔주었다. 내가 이 센서 값들을 그렇게 바꾸었기 때문
    private float x;

    @Column(nullable = false, name="y")
    private float y;

    @Column(nullable = false,name="time") // DB 수정하면서 이 부분이 추가되었다.
    private LocalDateTime time;

    protected Auto2D(){} // jpa를 사용하는 경우 이렇게 entity 객체에 기본적인 protected 생성자를 사용해야 한다.

    public Auto2D(float x, float y, LocalDateTime time) {
        this.x = x;
        this.y = y;
        this.time = time;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
