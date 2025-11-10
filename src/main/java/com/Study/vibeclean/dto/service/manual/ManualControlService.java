package com.Study.vibeclean.dto.service.manual;

import com.Study.vibeclean.domain.manual.ManualPower;
import com.Study.vibeclean.domain.manual.ManualSpeed;
import com.Study.vibeclean.domain.status.Status;
import com.Study.vibeclean.dto.manual.request.ManualPowerRequest;
import com.Study.vibeclean.dto.manual.request.ManualSpeedRequest;
import com.Study.vibeclean.dto.repository.manual.ManualPowerRepository;
import com.Study.vibeclean.dto.repository.manual.ManualSpeedRepository;
import com.Study.vibeclean.dto.repository.status.StatusRepository;
import com.Study.vibeclean.dto.service.mqtt.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManualControlService {
    private final ManualPowerRepository manualPowerRepository;
    private final ManualSpeedRepository manualSpeedRepository;
    private final StatusRepository statusRepository;
    private final MqttService mqttService;
    private String power;

    @Value("${mqtt.power-topic}")
    private String powerTopic;

    @Value("${mqtt.speed-topic}")
    private String speedTopic;

    // 수동조작 speed 들어온 값 STM으로 보내주는 메서드
    public void setSpeed(ManualSpeedRequest manualSpeed){
        Status latest = statusRepository.findTopByOrderByTimeDesc();
        if (latest == null || !"ON".equalsIgnoreCase(latest.getPower())) {
            return; // 오프라인 또는 OFF → 무시
        }
        manualSpeedRepository.save(new ManualSpeed(manualSpeed.getFanSpeed()));
        // 수정하고자 하는 값을 DB에 저장함.

        String payload = "{ \"fanSpeed\": " + manualSpeed.getFanSpeed() + " }";
        mqttService.publish(speedTopic, payload);
        // 해당 토픽으로 수동조작하고자 하는 스피드 값을 보내줌.

    }

    // 수동 조작으로 들어온 모드 변환 값
    public void setPower(ManualPowerRequest manualPower){
        power=manualPower.getPower();
        power = power.toUpperCase();
        Status latest = statusRepository.findTopByOrderByTimeDesc();

        if ("ON".equals(power)) {
            if (latest != null && "ON".equalsIgnoreCase(latest.getPower())) return;
            // ON 요청 → STM32로 송신/ 근데 이제 이미 ON 상태라면 무시
            manualPowerRepository.save(new ManualPower(power));
            mqttService.publish(powerTopic, "{ \"power\": \"ON\" }");
        } else if ("OFF".equals(power)) {
            if (latest == null) return;
            // 이미 꺼져있는데 off값이 들어온 경우라면 무시한다.
            manualPowerRepository.save(new ManualPower(power));
            mqttService.publish(powerTopic, "{ \"power\": \"OFF\" }");
        }


    }
}
