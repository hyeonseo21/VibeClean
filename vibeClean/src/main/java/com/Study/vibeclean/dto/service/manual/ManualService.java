package com.Study.vibeclean.dto.service.manual;

import com.Study.vibeclean.domain.manual.ManualPower;
import com.Study.vibeclean.domain.manual.ManualSpeed;
import com.Study.vibeclean.dto.manual.request.ManualPowerRequest;
import com.Study.vibeclean.dto.manual.request.ManualSpeedRequest;
import com.Study.vibeclean.dto.repository.manual.ManualPowerRepository;
import com.Study.vibeclean.dto.repository.manual.ManualSpeedRepository;
import org.springframework.stereotype.Service;

@Service
public class ManualService {
    private ManualPowerRepository manualPowerRepository;
    private ManualSpeedRepository manualSpeedRepository;

    public ManualService(ManualPowerRepository manualPowerRepository, ManualSpeedRepository manualSpeedRepository) {
        this.manualPowerRepository = manualPowerRepository;
        this.manualSpeedRepository = manualSpeedRepository;
    }

    public void setSpeed(ManualSpeedRequest manualSpeed){
        manualSpeedRepository.save(new ManualSpeed(manualSpeed.getSpeed()));

    }

    public void setPower(ManualPowerRequest manualPower){
        manualPowerRepository.save(new ManualPower(manualPower.getPower()));

    }
}
