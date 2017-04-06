package cs4347.group1.fauxstringinstrument;

public final class SensorUtil {

    public static final int SAME_NOTE_COUNT = 1;
    public static float[] prevGravity = {0.0f, 0.0f, 0.0f};

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

    /**
     * Demonstration on how to remove gravity component as given in Android docs
     * @param input The sensor data, from 0 to 2
     * @return Sensor data without gravity component
     */
    public static float[] removeGravity(float[] input){
        float[] result = new float[3];
        float alpha = 0.8f;

        for (int i = 0; i < 3; i++) {
            prevGravity[i] = lowPassFilter(input[i], prevGravity[i], alpha);
            result[i] = input[i] - prevGravity[i];
        }

        return result;
    }
}
