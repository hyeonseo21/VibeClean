package com.Study.vibeclean.dto.service.stm;

import com.Study.vibeclean.domain.manual.ManualMode;
import com.Study.vibeclean.domain.sensor.Sensor;
import com.Study.vibeclean.domain.status.Status;
import com.Study.vibeclean.dto.repository.manual.ManualModeRepository;
import com.Study.vibeclean.dto.repository.manual.ManualPowerRepository;
import com.Study.vibeclean.dto.repository.sensor.SensorRepository;
import com.Study.vibeclean.dto.repository.status.StatusRepository;
import com.Study.vibeclean.dto.stm.request.StmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StmService {
    private final SensorRepository sensorRepository;
    private final StatusRepository statusRepository;
    private final ManualModeRepository manualModeRepository;
    private final ManualPowerRepository manualPowerRepository;

    public void saveValue(StmRequest request){
        ManualMode manualMode = manualModeRepository.findTopByOrderByIdDesc();

        //수동조작 OFF 값이 들어오게 된다면 그러면 이제 해당 table을 초기화 시켜준다. 이래야 다시 ON을 받게 된다면 다시 그때부터 새 값을 기준으로 할 수 있다.
        if (Objects.equals(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(),"OFF")){
            sensorRepository.deleteAll();
            statusRepository.deleteAll();
            return;
        }

        statusRepository.save(new Status(manualPowerRepository.findTopByOrderByIdDesc().get().getPower(),request.getCurrentFloor(),request.getFanSpeed(),
                request.getPosition().getX(),request.getPosition().getY() ,LocalDateTime.now(),manualMode.getMode()));

        sensorRepository.save(new Sensor(request.getSensor().getX(),request.getSensor().getY(),
                request.getSensor().getZ()));
    }

}
