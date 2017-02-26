package cs4347.group1.fauxstringinstrument;

public class NoteUtil {
    public enum Note {
        C("C", 60),
        CS("C#/Db", 61),
        D("D", 62),
        DS("D#/Eb", 63),
        E("E", 64),
        F("F", 65),
        FS("F#/Gb", 66),
        G("G", 67),
        GS("G#/Ab", 68),
        A("A", 69),
        AS("A#/Bb", 70),
        B("B", 71),
        INVALID("-", -1);

        public final int number;
        public final String name;

        private Note(String name, int number) {
            this.name = name;
            this.number = number;
        }
    }

    public static final Note[] NOTES = {
            Note.C,
            Note.CS,
            Note.D,
            Note.DS,
            Note.E,
            Note.F,
            Note.FS,
            Note.G,
            Note.GS,
            Note.A,
            Note.AS,
            Note.B
    };

    public static Note getNote(float pitch, float roll) {
        if (Math.abs(pitch) > 50 || Math.abs(roll) < 10) return Note.INVALID;
        int index = ((int) Math.abs(roll) / 10) - 1;
        if (index < NOTES.length) return NOTES[index];
        return Note.INVALID;
    }
}
