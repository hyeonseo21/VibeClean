package com.Study.vibeclean.dto.mqtt;

import lombok.Data;

@Data
public class Auto2DMessage {
    private Auto2DMessage.Position position;

    @Data
    public static class Position {
        private float x;
        private float y;

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }


    public Position getPosition() {
        return position;
    }
}
