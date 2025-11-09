package com.Study.vibeclean.controller.manual;

import com.Study.vibeclean.domain.manual.ManualPower;
import com.Study.vibeclean.domain.manual.ManualSpeed;
import com.Study.vibeclean.dto.manual.request.ManualPowerRequest;
import com.Study.vibeclean.dto.manual.request.ManualSpeedRequest;
import com.Study.vibeclean.dto.service.manual.ManualService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManualController {
    private ManualService manualService;

    public ManualController(ManualService manualService) {
        this.manualService = manualService;
    }

    @PostMapping("/api/manual/speed")
    public void setSpeed(@RequestBody ManualSpeedRequest request){
        manualService.setSpeed(request);
    }

    @PostMapping("/api/manual/power")
    public void setSpeed(@RequestBody ManualPowerRequest request){
        manualService.setPower(request);
    }
}
