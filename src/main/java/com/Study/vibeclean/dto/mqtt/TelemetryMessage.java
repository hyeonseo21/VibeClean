package com.Study.vibeclean.dto.mqtt;

import lombok.Data;

@Data
public class TelemetryMessage {

    private String currentFloor;
    private int fanSpeed;

    private Position position;
    private SensorData sensor;

    @Data
    public static class Position {
        private int x;
        private int y;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    @Data
    public static class SensorData {
        private double x;
        private double y;
        private double z;

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
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
}