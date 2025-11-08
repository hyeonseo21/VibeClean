package com.Study.vibeclean.dto.stats.response;

import com.Study.vibeclean.dto.stats.Stats;
import org.springframework.jdbc.core.metadata.HsqlTableMetaDataProvider;

import java.util.List;

public class FloorDistribution {
    private double hardPercentage;
    private double carpetPercentage;
    private double dustySpotPercentage;
    private List<Stats> stats;

    public FloorDistribution(List<Stats> stats) {
        this.stats=stats;

        long totalNum= stats.size();
        long hardNum= stats.stream().filter(s -> "hard".equalsIgnoreCase(s.getStat())).count();
        long carpetNum = stats.stream().filter(s -> "carpet".equalsIgnoreCase(s.getStat())).count();
        long dustyNum = stats.stream().filter(s -> "dusty_spot".equalsIgnoreCase(s.getStat())).count();

        this.hardPercentage = ((double)hardNum/totalNum) * 100;
        this.carpetPercentage = ((double)carpetNum/totalNum) * 100;
        this.dustySpotPercentage = ((double)dustyNum/totalNum) * 100;
    }



    public double getHardPercentage() {
        return hardPercentage;
    }

    public double getCarpetPercentage() {
        return carpetPercentage;
    }

    public double getDustySpotPercentage() {
        return dustySpotPercentage;
    }
}
