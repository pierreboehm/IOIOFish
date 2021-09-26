package org.pb.android.ioiofish.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.pb.android.ioiofish.flow.FlowConfiguration;
import org.pb.android.ioiofish.flow.FlowManager;

@EBean(scope = EBean.Scope.Singleton)
public class ControlServiceManager {

    private static final String TAG = ControlServiceManager.class.getSimpleName();

    @RootContext
    Context context;

    @Bean
    FlowManager flowManager;

    private IOIOControlService controlService;

    public void startService(@Nullable FlowConfiguration flowConfiguration) {
        Log.d(TAG, "start control-service");
        flowManager.setup(flowConfiguration);
        startAndBindIOControlService();
    }

    public void stopService() {
        Log.d(TAG, "stop control-service");
        stopAndUnbindIOControlService();
    }

    public boolean serviceRunning() {
        return controlService.isRunning();
    }

    private void startAndBindIOControlService() {
        context.startService(IOIOControlService_.intent(context).get());
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
