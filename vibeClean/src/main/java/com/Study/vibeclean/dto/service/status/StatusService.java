package com.Study.vibeclean.dto.service.status;

import com.Study.vibeclean.domain.status.Status;
import com.Study.vibeclean.dto.repository.status.StatusRepository;
import com.Study.vibeclean.dto.status.request.Coordinate;
import com.Study.vibeclean.dto.status.request.RobotStatusRequest;
import com.Study.vibeclean.dto.status.response.RobotStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    private StatusRepository repository;

    public StatusService(StatusRepository repository) {
        this.repository = repository;
    }

    @Transactional //postman에서 값을 전달하면 저장하는 기능
    public void saveStatus(RobotStatusRequest request){
        int x = request.getCoordinate().getX();
        int y = request.getCoordinate().getY();

        repository.save(new Status(request.getPower(),request.getCurrentFloor(),request.getFanSpeed(),x,y));

    }

    @Transactional // 이 부분 짜는 거에서 살짝 막혔었다. postman을 활용해서 get으로 들어오면 아래의 상태 정보를 보내줌
    public RobotStatusResponse returnStatus(){
        Status latestStatus= repository.findTopByOrderByIdDesc().orElseThrow(()->new IllegalArgumentException("저장된 좌표 값이 존재하지 않습니다. ")); //가장 최신의 값을 가져옴.
        List<Coordinate> path= repository.findAllPathPoints();

        return new RobotStatusResponse(latestStatus.getPower(),latestStatus.getCurrentFloor(),latestStatus.getFanSpeed(),path);
    }

}
