package com.Study.vibeclean.dto.repository.sensor;

import com.Study.vibeclean.domain.sensor.Sensor;
import com.Study.vibeclean.domain.sensor.SensorBundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorBundleRepository extends JpaRepository<SensorBundle, Integer> {
    Optional<Sensor> findTopByOrderByIdDesc();

}
