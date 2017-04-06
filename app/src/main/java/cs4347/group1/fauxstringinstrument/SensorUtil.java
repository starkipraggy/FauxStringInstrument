package cs4347.group1.fauxstringinstrument;

public final class SensorUtil {

    public static final int SAME_NOTE_COUNT = 1;

    public static float degreeFromRadian(float radian) {
        double degree = -radian * 180.0 / Math.PI;
        return (float) degree;
    }

    /**
     * Low pass filter helper function. Run on a single float.
     * @param input The input from the phone sensor
     * @param prev Result from previous iteration
     * @param alpha Alpha value to use for calculation (see function notes)
     * @return The current result
     */
    // alpha is calculated as t / (t + dT)
    // with t, the low-pass filter's time-constant
    // and dT, the event delivery rate
    // fiddle around with the alpha value
    public static float lowPassFilter(float input, float prev, float alpha){
        return alpha * prev + (1 - alpha) * input;
    }

    /**
     * High pass filter helper function. Run on a single float.
     * @param input The input from the phone sensor
     * @param prev Result from previous iteration
     * @param prevInput The preceding input from the phone sensor
     * @param alpha Alpha value to use for calculation (see function notes)
     * @return The current result
     */
    // alpha is calculated as t / (t + dT)
    // with t, the low-pass filter's time-constant
    // and dT, the event delivery rate
    // fiddle around with the alpha value
    public static float highPassFilter(float input, float prev, float prevInput, float alpha){
        return alpha * (prev + input - prevInput);
    }
}
