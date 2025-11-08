package com.Study.vibeclean.dto.repository.manual;

import com.Study.vibeclean.domain.manual.ManualPower;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManualPowerRepository extends JpaRepository<ManualPower, Integer> {

}
