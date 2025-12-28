package com.Study.vibeclean.dto.service.mqtt;

import com.Study.vibeclean.domain.ai.Ai;
import com.Study.vibeclean.domain.auto2d.Auto2D;
import com.Study.vibeclean.domain.sensor.SensorBundle;
import com.Study.vibeclean.dto.mqtt.AiMessage;
import com.Study.vibeclean.dto.mqtt.Auto2DMessage;
import com.Study.vibeclean.dto.repository.ai.AiRepository;
import com.Study.vibeclean.dto.repository.auto2d.Auto2DRepository;
import com.Study.vibeclean.dto.repository.manual.ManualModeRepository;
import com.Study.vibeclean.dto.repository.manual.ManualPowerRepository;
import com.Study.vibeclean.dto.repository.sensor.SensorBundleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Study.vibeclean.domain.sensor.Sensor;
import com.Study.vibeclean.domain.status.Status;
import com.Study.vibeclean.dto.mqtt.TelemetryMessage;
import com.Study.vibeclean.dto.repository.sensor.SensorRepository;
import com.Study.vibeclean.dto.repository.status.StatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiHandler {
    // ObjectMapper는 Json문자열을 java객체로 변환해주는 Jackson라이브러리의 핵심 클래스이다.
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StatusRepository statusRepository;
    private final SensorRepository sensorRepository;
    private final SensorBundleRepository sensorBundleRepository;
    private final ManualModeRepository manualModeRepository;
    private final ManualPowerRepository manualPowerRepository;
    private final AiRepository aiRepository;
    private final Auto2DRepository auto2DRepository;
    private final RobotOfflineChecker robotOfflineChecker;


    public void AiTelemetry(String payload) {
        LocalDateTime now = LocalDateTime.now();
        //robotOfflineChecker.updateState(now);

        try {
            Ai first = aiRepository.findTopByOrderByTimeAsc();
            AiMessage msg = objectMapper.readValue(payload, AiMessage.class);

            //만약에 수동 조작으로 입력된 값이 OFF라면, 그러면 이제 FE가 요청하는 DB인 sensor와 status의 경우에 reset을 시켜줘야 한다.
            // 그래야 STM쪽에서 지속적으로 publish를 해주더라도 꺼졌다고 판단하여 기본 값으로 출력하게 만들 수 있다.
            if (Objects.equals(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(), "OFF")) {
                sensorRepository.deleteAll();
                statusRepository.deleteAll();
                sensorBundleRepository.deleteAll();
                aiRepository.deleteAll();
                auto2DRepository.deleteAll();
                return;

            }

            // 맨 처음에 들어온 값은 RobotOfflineChecker에 등록된 기본 값 UNKNOWN, 0이 저장되도록 함. 또, 이번에 바꾸면서 백엔드에서 좌표 값도 넣어줘야 하기 때문에 해당 부분도 넣는다.
            // 원래 기능 다 분리하고 해야하는데 지금 ㅋㅋ...하드코딩중이라;; 다 꼬였다. 우선 구현하고 나중에 리팩토링하자.
            Ai ai = new Ai(robotOfflineChecker.getPower(), robotOfflineChecker.getCurrentFloor(), robotOfflineChecker.getFanSpeed()
                    , LocalDateTime.now(), manualModeRepository.findTopByOrderByIdDesc().getMode(), msg.getSensor().getX(), msg.getSensor().getY(),
                    msg.getSensor().getZ());
            aiRepository.save(ai);

            RobotOfflineChecker.Position position = robotOfflineChecker.getPosition();
            if (position != null && !Objects.equals(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(), "OFF")) {
                Auto2D auto2D = new Auto2D(position.getX(), position.getY(), LocalDateTime.now());
                auto2DRepository.save(auto2D);
            }




            /*Ai ai= new Ai(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(),msg.getCurrentFloor(),msg.getFanSpeed()
                    ,LocalDateTime.now(),manualModeRepository.findTopByOrderByIdDesc().getMode(),msg.getSensor().getX(),msg.getSensor().getY(),
                    msg.getSensor().getZ());
            aiRepository.save(ai);*/

        } catch (Exception e) {
            log.error("Failed to handle telemetry payload: {}", payload, e);
        }
    }
}
