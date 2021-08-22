package org.pb.android.ioiofish.flow;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class FlowManager {

    private float azimuth, pitch, roll;

    public void setup(FlowConfiguration configuration) {

    }

    public void updateRotation(float azimuth, float pitch, float roll) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
    }

}
