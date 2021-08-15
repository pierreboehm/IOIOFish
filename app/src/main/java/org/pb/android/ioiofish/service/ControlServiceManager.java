package org.pb.android.ioiofish.service;

import android.content.Context;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean(scope = EBean.Scope.Singleton)
public class ControlServiceManager {

    private static final String TAG = ControlServiceManager.class.getSimpleName();

    @RootContext
    Context context;

    public void startService() {
        Log.d(TAG, "start control-service");
        context.startService(IOIOControlService_.intent(context).get());
    }

    public void stopService() {
        Log.d(TAG, "stop control-service");
        context.stopService(IOIOControlService_.intent(context).get());
    }
}
