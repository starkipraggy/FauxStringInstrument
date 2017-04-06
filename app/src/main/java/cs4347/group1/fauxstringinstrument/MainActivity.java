package cs4347.group1.fauxstringinstrument;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_note)
    TextView tvNote;

    @BindView(R.id.button_hold_note)
    Button buttonHoldNote;

    @BindView(R.id.button_play)
    Button buttonPlay;

    @BindView(R.id.button_piano)
    Button buttonPiano;

    @BindView(R.id.button_vibraphone)
    Button buttonVibraphone;

    @OnClick(R.id.button_play)
    void onClickPlay() {
        canPlay = !canPlay;
        buttonPlay.setText(canPlay ? R.string.button_pause : R.string.button_play);
    }

    @OnClick(R.id.button_piano)
    void changeToPiano() {
        changeInstrument(GeneralMidiConstants.ACOUSTIC_GRAND_PIANO);
    }

    @OnClick(R.id.button_vibraphone)
    void changeToVibraphone() {
        changeInstrument(GeneralMidiConstants.VIBRAPHONE);
    }

    @State
    int currentNoteNumber;

    private SensorManager sensorManager;

    private MidiDriver midiDriver;
    private byte[] event;
    // private byte currentInstrument;

    private NoteUtil.Note previousNote = NoteUtil.Note.INVALID;
    private int sameNoteCount;

    private boolean isHoldingNote;

    private boolean canPlay;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float ax = event.values[0];
                float ay = event.values[1];
                float az = event.values[2];

                float anglexy = (float) (Math.atan2(ax, ay) / (Math.PI / 180));
                float anglexz = (float) (Math.atan2(ax, az) / (Math.PI / 180));
                float angleyz = (float) (Math.atan2(ay, az) / (Math.PI / 180));

                play(anglexy);
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
        midiDriver = new MidiDriver();
        buttonHoldNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isHoldingNote = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isHoldingNote = false;
                }
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        midiDriver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        midiDriver.stop();
    }

    private void play(float angle) {
        NoteUtil.Note note = NoteUtil.getNote(angle);
        if (note != previousNote) {
            sameNoteCount = 0;
        } else {
            sameNoteCount++;
        }
        if (sameNoteCount > SensorUtil.SAME_NOTE_COUNT) {
            if (!isHoldingNote && note.number != currentNoteNumber) {
                stopNote(currentNoteNumber);
                currentNoteNumber = note.number;
                playNote(currentNoteNumber);
            }
            tvNote.setText(note.name);
        }
        previousNote = note;
    }

    private void changeInstrument(byte instrument){
        event = new byte[2];
        event[0] = (byte) 0xC0;
        event[1] = instrument;
        midiDriver.write(event);
    }

    private void playNote(int noteNumber) {
        if (!canPlay || noteNumber < 0) return;

        // Construct a note ON message for the note at maximum velocity on channel 1:
//        event = new byte[3];
//        event[0] = (byte) 0xC0;
//        event[1] = (byte) GeneralMidiConstants.VIBRAPHONE;
//        event[2] = (byte) 0x00;
//        midiDriver.write(event);

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
