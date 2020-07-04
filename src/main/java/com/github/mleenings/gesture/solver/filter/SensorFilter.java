package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

public interface SensorFilter {
    /**
     * @param smd
     * @return the filtered SensorMotionData
     */
    SensorMotionData filter(SensorMotionData smd);
}
