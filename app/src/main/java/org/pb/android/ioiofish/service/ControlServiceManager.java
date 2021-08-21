package org.pb.android.ioiofish.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean(scope = EBean.Scope.Singleton)
public class ControlServiceManager {

    private static final String TAG = ControlServiceManager.class.getSimpleName();

    @RootContext
    Context context;

    private IOIOControlService controlService;

    public void startService() {
        Log.d(TAG, "start control-service");
        startAndBindIOControlService();
    }

    public void stopService() {
        Log.d(TAG, "stop control-service");
        stopAndUnbindIOControlService();
    }

    private void startAndBindIOControlService() {
        context.bindService(IOIOControlService_.intent(context).get(), serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private void stopAndUnbindIOControlService() {
        controlService.stopService();
        context.unbindService(serviceConnection);
        context.stopService(IOIOControlService_.intent(context).get());
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            IOIOControlService.LocalBinder localBinder = (IOIOControlService.LocalBinder) iBinder;
            controlService = localBinder.getService();
            controlService.startService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            controlService = null;
        }
    };
}
