package com.Study.vibeclean.dto.repository.ai;

import com.Study.vibeclean.domain.ai.Ai;
import com.Study.vibeclean.domain.sensor.Sensor;
import com.Study.vibeclean.domain.status.Status;
import com.Study.vibeclean.dto.mqtt.AiMessage;
import com.Study.vibeclean.dto.stats.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AiRepository extends JpaRepository<Ai,Integer> {
    Ai findTopByOrderByTimeDesc();
    Ai findTopByOrderByTimeAsc();
    Optional<Ai> findTopByOrderByIdDesc();
    @Query("SELECT new com.Study.vibeclean.dto.stats.Stats(a.currentFloor) FROM Ai a")
    List<Stats> findAllByCurrentFloor();
}
