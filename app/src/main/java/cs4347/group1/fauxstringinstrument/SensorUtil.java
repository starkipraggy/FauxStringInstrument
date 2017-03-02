package cs4347.group1.fauxstringinstrument;

public final class SensorUtil {

    public static float degreeFromRadian(float radian) {
        double degree = -radian * 180.0 / Math.PI;
        return (float) degree;
    }
}
