package org.pb.android.ioiofish.event;

public class Events {

    public static class RotationChangedEvent {
        private final float azimut, pitch, roll;

        public RotationChangedEvent(float azimut, float pitch, float roll) {
            this.azimut = azimut;
            this.pitch = pitch;
            this.roll = roll;
        }

        public float getAzimut() {
            return azimut;
        }

        public float getPitch() {
            return pitch;
        }

        public float getRoll() {
            return roll;
        }
    }

    public static class PluggedStateChangedEvent {
        private final boolean plugged;

        public PluggedStateChangedEvent(boolean plugged) {
            this.plugged = plugged;
        }

        public boolean isPlugged() {
            return plugged;
        }
    }

    public static class ValueChangedEvent {
        private final String value;

        public ValueChangedEvent(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getIntValue() {
            return Integer.parseInt(value);
        }
    }
}
