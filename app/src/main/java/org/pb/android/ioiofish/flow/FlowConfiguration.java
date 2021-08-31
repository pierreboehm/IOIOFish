package org.pb.android.ioiofish.flow;

import org.pb.android.ioiofish.pin.AnalogInputPin;
import org.pb.android.ioiofish.pin.DigitalInputPin;
import org.pb.android.ioiofish.pin.DigitalOutputPin;
import org.pb.android.ioiofish.pin.IOIO_Pin;

import java.util.ArrayList;
import java.util.List;

public class FlowConfiguration {

    public final List<IOIO_Pin> analogInputPins = new ArrayList<>();
    public final List<IOIO_Pin> digitalInputPins = new ArrayList<>();
    public final List<IOIO_Pin> digitalOutputPins = new ArrayList<>();

    private FlowConfiguration(Builder builder) {
        analogInputPins.addAll(builder.analogInputPins);
        digitalInputPins.addAll(builder.digitalInputPins);
        digitalOutputPins.addAll(builder.digitalOutputPins);
    }

    public static class Builder {

        private List<IOIO_Pin> analogInputPins = new ArrayList<>();
        private List<IOIO_Pin> digitalInputPins = new ArrayList<>();
        private List<IOIO_Pin> digitalOutputPins = new ArrayList<>();

        public Builder addAnalogInputPin(FlowManager.PinConfiguration pinConfiguration) {
            AnalogInputPin analogInputPin = new AnalogInputPin(pinConfiguration);
            analogInputPins.add(analogInputPin);
            return this;
        }

        public Builder addDigitalInputPin(int pinNumber, FlowManager.PinConfiguration pinConfiguration) {
            DigitalInputPin digitalInputPin = new DigitalInputPin(pinConfiguration);
            digitalInputPins.add(digitalInputPin);
            return this;
        }

        public Builder addDigitalOutputPin(FlowManager.PinConfiguration pinConfiguration) {
            DigitalOutputPin digitalOutputPin = new DigitalOutputPin(pinConfiguration);
            digitalOutputPins.add(digitalOutputPin);
            return this;
        }

        public FlowConfiguration getConfiguration() {
            return new FlowConfiguration(this);
        }
    }

}
