package com.Study.vibeclean.dto.stats.response;

public class StatsResponse {
    private int totalRuntimeSeconds=3600;
    private FloorDistribution floorDistribution;

    // 원래라면, 시간도 측정해서 이제 해당 값을 생성자 보내줘야 하는데 아직까지는 우리가 직접 그거를 주기적으로 값ㅇ르
    // 줄 수가 없기 때문에 임의의 값을 넣고 만듦. 나중에 추가 구현 필요
    public StatsResponse(FloorDistribution floorDistribution) {
        this.floorDistribution = floorDistribution;
    }

    public FloorDistribution getFloorDistribution() {
        return floorDistribution;
    }
    public int getTotalRuntimeSeconds() {
        return totalRuntimeSeconds;
    }


}
