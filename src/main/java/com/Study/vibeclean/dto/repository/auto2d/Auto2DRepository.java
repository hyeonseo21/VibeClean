package com.Study.vibeclean.dto.repository.auto2d;

import com.Study.vibeclean.domain.ai.Ai;
import com.Study.vibeclean.domain.auto2d.Auto2D;
import com.Study.vibeclean.dto.status.request.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface Auto2DRepository extends JpaRepository<Auto2D,Integer> {
    @Query("""
   select new com.Study.vibeclean.dto.status.request.Coordinate(s.x, s.y)
   from Auto2D s
   order by s.id asc
   """)
    List<Coordinate> findAllPathPoints();

    Auto2D findTopByOrderByIdDesc();
}
