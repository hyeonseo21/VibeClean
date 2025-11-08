package com.Study.vibeclean.dto.service.stats;

import com.Study.vibeclean.dto.repository.stats.StatsRepository;
import com.Study.vibeclean.dto.stats.Stats;
import com.Study.vibeclean.dto.stats.response.FloorDistribution;
import com.Study.vibeclean.dto.stats.response.StatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatsService {
    private StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional
    public StatsResponse RobotStatus(){
        List<Stats> stats=statsRepository.findAllByCurrentFloor();
        return new StatsResponse(new FloorDistribution(stats));
    }
}
