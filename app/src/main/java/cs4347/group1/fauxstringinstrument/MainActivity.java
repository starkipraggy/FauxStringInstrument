package cs4347.group1.fauxstringinstrument;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_note)
    TextView tvNote;

    @State
    int currentNoteNumber;

    private SensorManager sensorManager;
    private final float[] rotationMatrix = new float[9];
    private final float[] remappedMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    private MidiDriver midiDriver;
    private byte[] event;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                updateOrientation();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // ignore
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        resetRotationMatrix();
        midiDriver = new MidiDriver();
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST);
        midiDriver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        midiDriver.stop();
    }

    private void resetRotationMatrix() {
        rotationMatrix[0] = 1;
        rotationMatrix[4] = 1;
        rotationMatrix[8] = 1;
    }

    private void updateOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedMatrix);
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Z,
                        SensorManager.AXIS_MINUS_X,
                        remappedMatrix);
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_X,
                        SensorManager.AXIS_MINUS_Z,
                        remappedMatrix);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_Z,
                        SensorManager.AXIS_X,
                        remappedMatrix);
                break;
        }
        SensorManager.getOrientation(remappedMatrix, orientationAngles);
        float pitch = Math.abs(SensorUtil.degreeFromRadian(orientationAngles[1]));
        float roll = Math.abs(SensorUtil.degreeFromRadian(orientationAngles[2]));
        play(pitch, roll);
    }

    private void play(float pitch, float roll) {
        NoteUtil.Note note = NoteUtil.getNote(pitch, roll);
        if (note.number != currentNoteNumber) {
            stopNote(currentNoteNumber);
            tvNote.setText(note.name);
            currentNoteNumber = note.number;
            playNote(currentNoteNumber);
        }
    }

    private void playNote(int noteNumber) {
        if (noteNumber < 0) return;

        // Construct a note ON message for the note at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) noteNumber;
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
    }


    private void stopNote(int noteNumber) {
        if (noteNumber < 0) return;

        // Construct a note OFF message for the note at minimum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = (byte) noteNumber;
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
    }
}
