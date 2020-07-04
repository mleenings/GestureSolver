package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorData;

public class SimpleLowPassFilter {
    private static final float ALPHA = 0.2f;
    private float[] filtered = new float[3];

    /**
     *
     * @param sd
     * @return SensorData low pass filtered
     */
    public SensorData filter(SensorData sd) {
        SensorData copySd = new SensorData(sd);
        float[] v = sd.getValues();
        for(int i = 0; i<v.length;i++){
            filtered[i] = filtered[i] + ALPHA * (v[i] - filtered[i]);
        }
        copySd.setValues(filtered);
        return copySd;
    }
}
