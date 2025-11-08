package com.Study.vibeclean.controller.sensor;

import com.Study.vibeclean.domain.sensor.Sensor;
import com.Study.vibeclean.dto.sensor.request.SensorRequest;
import com.Study.vibeclean.dto.sensor.response.SensorResponse;
import com.Study.vibeclean.dto.service.sensor.SensorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensorController {
    private SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/api/robot/sensor") // 지금은 제대로 작동하는지 확인하기 위해서 임의로 이렇게 값을 내가 DB에 넣는 방식으로 구현했다.
    public void saveSensorValue(@RequestBody SensorRequest request){
        sensorService.saveSensorValue(request);

    }

    @GetMapping("/api/robot/sensor")
    public Sensor getSensorValue(){
        Sensor response=sensorService.getSensorValue();
        return response;
    }

}
