package com.Study.vibeclean.dto.mqtt;

import lombok.Data;

import java.util.List;

@Data
public class TelemetryMessage {

    private String currentFloor;
    private int fanSpeed;

    private Position position;
    private SensorData sensor;
    //private List<SensorData> sensor;

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

    public String getCurrentFloor() {
        return currentFloor;
    }

    public int getFanSpeed() {
        return fanSpeed;
    }

    public Position getPosition() {
        return position;
    }

    public SensorData getSensor() {
        return sensor;
    }

   /* public List<SensorData> getSensor() {
        return sensor;
    }*/
}
