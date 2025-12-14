package com.Study.vibeclean.dto.status.request;
// x,y좌표 값을 입력받기 위한 클래스

public class Coordinate {
    private float x;
    private float y;

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
