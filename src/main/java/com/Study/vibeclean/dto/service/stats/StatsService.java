package com.Study.vibeclean.dto.service.stats;

import com.Study.vibeclean.domain.ai.Ai;
import com.Study.vibeclean.domain.status.Status;
import com.Study.vibeclean.dto.repository.ai.AiRepository;
import com.Study.vibeclean.dto.repository.stats.StatsRepository;
import com.Study.vibeclean.dto.stats.Stats;
import com.Study.vibeclean.dto.stats.response.AiStatsResponse;
import com.Study.vibeclean.dto.stats.response.FloorDistribution;
import com.Study.vibeclean.dto.stats.response.StatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StatsService {
    private StatsRepository statsRepository;
    private AiRepository aiRepository;

    public StatsService(StatsRepository statsRepository, AiRepository aiRepository) {
        this.statsRepository = statsRepository;
        this.aiRepository = aiRepository;
    }

    @Transactional
    public StatsResponse RobotStatus(){
        Status statusInitial = statsRepository.findTopByOrderByTimeAsc();
        Status statusFinal =statsRepository.findTopByOrderByTimeDesc();
        List<Stats> stats=statsRepository.findAllByCurrentFloor();

        if (stats.isEmpty()){ // 만약 비어있다면 즉 아무 값도 없다면 이렇게 기본 값 리턴
            return new StatsResponse(new FloorDistribution(new ArrayList<>()),LocalDateTime.now(), LocalDateTime.now());
        }
        /*else if (Objects.equals(statusFinal.getPower(), "OFF")){
            return new StatsResponse(new FloorDistribution(new ArrayList<>()),LocalDateTime.now(), LocalDateTime.now());
        }*/

        return new StatsResponse(new FloorDistribution(stats),statusInitial.getTime(),statusFinal.getTime());
    }

    //보드 2개로 바꾼 버전
    @Transactional
    public AiStatsResponse AiStatus(){
        Ai statusInitial = aiRepository.findTopByOrderByTimeAsc();
        Ai statusFinal =aiRepository.findTopByOrderByTimeDesc();
        List<Stats> stats=aiRepository.findAllByCurrentFloor();

        if (stats.isEmpty()){ // 만약 비어있다면 즉 아무 값도 없다면 이렇게 기본 값 리턴
            return new AiStatsResponse(new FloorDistribution(new ArrayList<>()),LocalDateTime.now(), LocalDateTime.now());
        }
        /*else if (Objects.equals(statusFinal.getPower(), "OFF")){
            return new AiStatsResponse(new FloorDistribution(new ArrayList<>()),LocalDateTime.now(), LocalDateTime.now());
        }*/

        return new AiStatsResponse(new FloorDistribution(stats),statusInitial.getTime(),statusFinal.getTime());

    }
}
