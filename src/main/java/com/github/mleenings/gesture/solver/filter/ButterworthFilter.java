package com.github.mleenings.gesture.solver.filter;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

/** Butterworth low-pass filter to reduce the noise */
public abstract class ButterworthFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ButterworthFilter.class);
  private int rate;

  /** constructor */
  public ButterworthFilter() {
    this.rate = getActualDelay();
  }

  /**
   * The actual measured rate/delay. Android example: final Class cls =
   * Class.forName("android.hardware.SensorManager"); final Method method =
   * cls.getDeclaredMethod("getDelay",new Class[]{Integer.TYPE}); method.setAccessible(true); delay
   * = (Integer) method.invoke(cls, MotionSensorScannner.SENSOR_DELAY);
   *
   * @return the acutal measured rate in microseconds
   */
  protected abstract int getActualDelay();

  /**
   * @param data
   * @return the filtered vector
   */
  public float[] filter(float[] data) {
    double delaySampling = this.rate; // in microseconds
    double frequenzSampling = 1 / (delaySampling / 1000000); // in Hz
    double cutoffFreq = 5; // in Hz
    FilterPassType filterPassType = FilterPassType.lowpass;
    int filterOrder = 6;
    double fcf1 = cutoffFreq / frequenzSampling;
    double fcf2 = -1.0; // ignored for lowpass/highpass
    IirFilter filter =
        new IirFilter(IirFilterDesignExstrom.design(filterPassType, filterOrder, fcf1, fcf2));
    float[] filteredData = new float[data.length];
    for (int i = 0; i < data.length; i++) {
      filteredData[i] = (float) filter.step(data[i]);
    }
    return filteredData;
  }
}
