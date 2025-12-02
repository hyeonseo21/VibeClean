package com.Study.vibeclean.dto.service.mqtt;

import com.Study.vibeclean.domain.sensor.SensorBundle;
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

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryHandler {
    // ObjectMapper는 Json문자열을 java객체로 변환해주는 Jackson라이브러리의 핵심 클래스이다.
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StatusRepository statusRepository;
    private final SensorRepository sensorRepository;
    private final SensorBundleRepository sensorBundleRepository;
    private final ManualModeRepository manualModeRepository;
    private final ManualPowerRepository manualPowerRepository;

    public void handleTelemetry(String payload) {
        try {// 이 바로 아래 코드로 json문자열을 java 객체로 만들어준다. 매우 편리 ㅎㅎ
            TelemetryMessage msg = objectMapper.readValue(payload, TelemetryMessage.class);

            //만약에 수동 조작으로 입력된 값이 OFF라면, 그러면 이제 FE가 요청하는 DB인 sensor와 status의 경우에 reset을 시켜줘야 한다.
            // 그래야 STM쪽에서 지속적으로 publish를 해주더라도 꺼졌다고 판단하여 기본 값으로 출력하게 만들 수 있다.
            if(Objects.equals(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(),"OFF")){
                sensorRepository.deleteAll();
                statusRepository.deleteAll();
                sensorBundleRepository.deleteAll();
                return ;

            }

            /*if (Objects.equals(msg.getCurrentFloor(), "UNKNOWN")){
                return;
            } // wifi 모듈을 2개를 사용해야 한다면
*/
            // 1) status 저장
            Status status = new Status(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(), msg.getCurrentFloor(),msg.getFanSpeed(),
                    msg.getPosition().getX(),msg.getPosition().getY(),LocalDateTime.now(),
                    manualModeRepository.findTopByOrderByIdDesc().getMode());
            statusRepository.save(status);

            // 2) sensor_bundle 저장 (최근 10개)
            //    msg.getSensor() 는 List<TelemetryMessage.SensorData>
            /*if (msg.getSensor() != null && !msg.getSensor().isEmpty()) {

                // 매번 새로 받는 10개만 유지하고 싶다면, 먼저 기존 데이터 삭제
                sensorBundleRepository.deleteAll();

                // 하나씩 SensorBundle로 변환하여 저장
                for (TelemetryMessage.SensorData sd : msg.getSensor()) {
                    SensorBundle bundle = new SensorBundle(sd.getX(), sd.getY(), sd.getZ());
                    sensorBundleRepository.save(bundle);
                }
            }*/
            TelemetryMessage.SensorData sd = msg.getSensor();
            if (sd != null && !Objects.equals(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(), "OFF")) {
                Sensor sensor = new Sensor(sd.getX(),sd.getY(),sd.getZ());
                sensorRepository.save(sensor);
            }

        } catch (Exception e) {
            log.error("Failed to handle telemetry payload: {}", payload, e);
        }
    }
}
