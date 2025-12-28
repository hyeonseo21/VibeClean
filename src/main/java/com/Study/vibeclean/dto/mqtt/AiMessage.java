package com.Study.vibeclean.dto.mqtt;

import lombok.Data;

@Data
public class AiMessage {
    private AiMessage.SensorData sensor;
    private String currentFloor;
    private int fanSpeed;

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

    public SensorData getSensor() {
        return sensor;
    }

    public String getCurrentFloor() {
        return currentFloor;
    }

    public int getFanSpeed() {
        return fanSpeed;
    }
}
