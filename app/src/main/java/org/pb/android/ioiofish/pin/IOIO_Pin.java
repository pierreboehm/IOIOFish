package org.pb.android.ioiofish.pin;

import org.pb.android.ioiofish.flow.FlowManager;

public abstract class IOIO_Pin {

    public abstract int getPinNumber();

    public abstract FlowManager.PinConfiguration getPinConfiguration();

    public abstract FlowManager.PinType getPinType();

}
