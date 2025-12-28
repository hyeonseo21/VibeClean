package com.Study.vibeclean.dto.service.mqtt;

import com.Study.vibeclean.domain.ai.Ai;
import com.Study.vibeclean.domain.manual.ManualDirection;
import com.Study.vibeclean.domain.manual.ManualMode;
import com.Study.vibeclean.domain.manual.ManualPower;
import com.Study.vibeclean.domain.status.Status;
import com.Study.vibeclean.dto.mqtt.Ai2DMessage;
import com.Study.vibeclean.dto.repository.ai.AiRepository;
import com.Study.vibeclean.dto.repository.auto2d.Auto2DRepository;
import com.Study.vibeclean.dto.repository.manual.ManualDirectionRepository;
import com.Study.vibeclean.dto.repository.manual.ManualModeRepository;
import com.Study.vibeclean.dto.repository.manual.ManualPowerRepository;
import com.Study.vibeclean.dto.repository.manual.ManualSpeedRepository;
import com.Study.vibeclean.dto.repository.sensor.SensorBundleRepository;
import com.Study.vibeclean.dto.repository.sensor.SensorRepository;
import com.Study.vibeclean.dto.repository.status.StatusRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Duration;


import java.time.LocalDateTime;

@Slf4j
@Service
@EnableScheduling // 스프링 부트에서 주기적인 작업(스케쥴러)를 실행하기 위해 필요한 어노테이션
@RequiredArgsConstructor
public class RobotOfflineChecker {
    private final StatusRepository statusRepository;
    private final SensorRepository sensorRepository;
    private final ManualSpeedRepository manualSpeedRepository;
    private final ManualPowerRepository manualPowerRepository;
    private final ManualModeRepository manualModeRepository;
    private final ManualDirectionRepository manualDirectionRepository;
    private final SensorBundleRepository sensorBundleRepository;
    private final AiRepository aiRepository;
    private final Auto2DRepository auto2DRepository;

    private String currentFloor = "UNKNOWN";
    private int fanSpeed = 0;
    private RobotOfflineChecker.Position position = new Position();
    private String power = "OFF";

    @Data
    public static class Position {
        private float x = 0.0f;
        private float y = 0.3f;

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }
    }


    public RobotOfflineChecker.Position getPosition() {
        return position;
    }


    public String getCurrentFloor() {
        return currentFloor;
    }


    public int getFanSpeed() {
        return fanSpeed;
    }

    public String getPower() {
        return power;
    }

    /*public void updateState(LocalDateTime now) {
        Ai first = aiRepository.findTopByOrderByTimeAsc();
        if (first == null) return;

        long sec = Duration.between(first.getTime(), now).getSeconds();

        if (sec <= 5) { currentFloor="UNKNOWN"; fanSpeed=0; position.setX(0); position.setY(0); power="OFF"; }
        else if (sec <= 13) { currentFloor="Hard"; fanSpeed=1; position.setY(position.getY()+0.15f); power="ON"; }
        else if (sec<=19){currentFloor = "Hard";fanSpeed = 1;position.setX(position.getX()+0.05f);power="ON";}
        else if (sec<=24){currentFloor = "Carpet";fanSpeed = 2;position.setY(position.getY()-0.15f);power="ON";}
        else if (sec<=31){currentFloor = "Hard";fanSpeed = 1;position.setX(position.getX()+0.05f);power="ON";}
        else if (sec<=38){currentFloor = "Carpet";fanSpeed = 2;position.setY(position.getY()+0.15f);power="ON";}
        else if (sec<=45){currentFloor = "Hard";fanSpeed = 1;position.setX(position.getX()+0.05f);power="ON";}
        else if (sec<=52){currentFloor = "Dusty";fanSpeed = 3;position.setY(position.getY()-0.15f);power="ON";}
        else{currentFloor = "Hard";fanSpeed = 1;power = "ON";position.setY(position.getY()-0.15f);}
    }*/

    @Scheduled(fixedDelay = 1000) // 이거를 통해 스프링부트가 1초마다 반복 실행되게 만든다.
    public void checkOffline() {
        Ai first = aiRepository.findTopByOrderByTimeAsc(); //가장 맨 처음에 저장된 값을 의미한다.
        Ai latest = aiRepository.findTopByOrderByTimeDesc();
        if (first == null) {
            return; // 이미 초기 상태
        }

        LocalDateTime now = LocalDateTime.now();

        if (first != null) {
            long secondsFromFirstToNow = Duration.between(first.getTime(), now).getSeconds();

            if (secondsFromFirstToNow <=5) {
                currentFloor = "UNKNOWN";
                fanSpeed = 0;
                position.setX(0.0f);
                position.setY(0.3f);
                power="OFF";

            }
            else if (secondsFromFirstToNow<=13){
                currentFloor = "Hard";
                fanSpeed = 1;
                position.setY(position.getY()+0.2f);
                power="ON";

            }

            else if (secondsFromFirstToNow<=19){
                currentFloor = "Hard";
                fanSpeed = 1;
                position.setX(position.getX()+0.025f);
                power="ON";

            }

            else if (secondsFromFirstToNow<=24){
                currentFloor = "Carpet";
                fanSpeed = 2;
                position.setY(position.getY()-0.3f);
                power="ON";

            }

            else if (secondsFromFirstToNow<=31){
                currentFloor = "Hard";
                fanSpeed = 1;
                position.setX(position.getX()+0.025f);
                power="ON";

            }

            else if (secondsFromFirstToNow<=38){
                currentFloor = "Carpet";
                fanSpeed = 2;
                position.setX(position.getX()+0.03f);
                position.setY(position.getY()+0.2f);
                power="ON";
            }

            else if (secondsFromFirstToNow<=45){
                currentFloor = "Hard";
                fanSpeed = 1;
                position.setX(position.getX()+0.025f);
                power="ON";
            }

            else if (secondsFromFirstToNow<=52){
                currentFloor = "Dusty";
                fanSpeed = 3;
                position.setY(position.getY()-0.22f);
                power="ON";
            }

            else if (secondsFromFirstToNow<=56){
                currentFloor = "Hard";
                fanSpeed = 1;
                position.setX(position.getX()+0.025f);
                power="ON";
            }

            else {
                currentFloor = "Hard";
                fanSpeed = 1;
                position.setX(position.getX()+0.03f);
                position.setY(position.getY()+0.2f);
                power="ON";
            }


        }

        if (latest.getTime().isBefore(now.minusSeconds(180))) { // 가장 최근에 status에 저장된 시간과 현재 시간이 3초가 넘으면 일어나는 일
            // 지금은 테스트 단계라서 59초로 설정했지만, 실제로 할 때는 3초나 더 간격 작게 해서 고고링
            log.info("No telemetry for 3s → Robot OFF → clear tables");
            statusRepository.deleteAll();
            sensorRepository.deleteAll();// 꺼졌다고 판단하여 로그에 출력 및 db에 저장된 모든 값들을 삭제한다.
            sensorBundleRepository.deleteAll();
            manualPowerRepository.deleteAll();
            manualSpeedRepository.deleteAll();
            manualDirectionRepository.deleteAll();
            manualModeRepository.deleteAll();
            manualModeRepository.save(new ManualMode("AUTO")); // 수동 조작을 할 때, mode를 다루는 경우, 기본 초기화 값 자체가 auto가 들어간 상태이므로,
            // 모든 행을 다 삭제한 후에, 기본 값인 AUTO를 넣어둔다.
            manualPowerRepository.save(new ManualPower("ON"));
            //59초가 지나면 기본 값으로 ON을 설정한다.
            aiRepository.deleteAll();
            auto2DRepository.deleteAll();

        }
    }
}
