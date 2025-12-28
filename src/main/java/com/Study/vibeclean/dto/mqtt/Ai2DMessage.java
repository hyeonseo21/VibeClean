package com.Study.vibeclean.dto.mqtt;


import lombok.Data;

@Data
public class Ai2DMessage {
    private Ai2DMessage.SensorData sensor;
    private String currentFloor;
    private int fanSpeed;
    private Ai2DMessage.Position position;

    @Data
    public static class SensorData {
        private float x;
        private float y;
        private float z;

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

    public Ai2DMessage.SensorData getSensor() {
        return sensor;
    }

    public String getCurrentFloor() {
        return currentFloor;
    }

    public int getFanSpeed() {
        return fanSpeed;
    }
    
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


    public Ai2DMessage.Position getPosition() {
        return position;
    }
}

