package com.Study.vibeclean.dto.service.sensor;

import com.Study.vibeclean.domain.sensor.Sensor;
import com.Study.vibeclean.dto.repository.sensor.SensorRepository;
import com.Study.vibeclean.dto.sensor.request.SensorRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SensorService {
    private SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Transactional
    public void saveSensorValue(SensorRequest request){
        sensorRepository.save(new Sensor(request.getX(),request.getY(),request.getZ())); // 중간 끝나고 다시 짜면서 많이 까먹었네;; jpa통해서 save할 때는, 객체로 저장해야 한다. 그리고 그 객체를 domain에다가 설정하는 것이고


    }

    @Transactional
    public Sensor getSensorValue(){
        Sensor response= sensorRepository.findTopByOrderByIdDesc().orElseThrow(()->new IllegalArgumentException("저장된 값이 없습니다."));
        return response;

    }
}
