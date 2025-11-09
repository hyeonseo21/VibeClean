package com.Study.vibeclean.controller.status;

import com.Study.vibeclean.dto.service.status.StatusService;
import com.Study.vibeclean.dto.status.request.RobotStatusRequest;
import com.Study.vibeclean.dto.status.response.RobotStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    private StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping("/api/robot/status")
    public void RobotStatus(@RequestBody RobotStatusRequest request){
        statusService.saveStatus(request);
    }

    @GetMapping("/api/robot/status")
    public RobotStatusResponse SendRobotStatus(){
        RobotStatusResponse responses=statusService.returnStatus();
        return responses;

    }
}
