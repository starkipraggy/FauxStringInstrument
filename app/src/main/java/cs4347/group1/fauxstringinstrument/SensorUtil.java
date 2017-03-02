package cs4347.group1.fauxstringinstrument;

public final class SensorUtil {

    public static float degreeFromRadian(float radian) {
        double degree = -radian * Math.PI / 180.0;
        return (float) degree;
    }
}
