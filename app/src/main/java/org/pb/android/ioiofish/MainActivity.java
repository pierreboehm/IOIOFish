package org.pb.android.ioiofish;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pb.android.ioiofish.event.Events;
import org.pb.android.ioiofish.flow.FlowConfiguration;
import org.pb.android.ioiofish.flow.FlowManager;
import org.pb.android.ioiofish.fragment.StateZoomFragment;
import org.pb.android.ioiofish.fragment.StateZoomFragment_;
import org.pb.android.ioiofish.service.ControlServiceManager;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bean
    ControlServiceManager controlServiceManager;

    private Toast closeAppToast = null;

    @AfterViews
    public void initViews() {
        StateZoomFragment stateZoomFragment = StateZoomFragment_.builder().build();
        setFragment(stateZoomFragment, StateZoomFragment.TAG);

        //InfoFragment infoFragment = InfoFragment_.builder().build();
        //setFragment(infoFragment, InfoFragment.TAG);

        //CalibrateFragment calibrateFragment = CalibrateFragment_.builder().build();
        //setFragment(calibrateFragment, CalibrateFragment.TAG);
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (controlServiceManager.serviceRunning()) {
            controlServiceManager.stopService();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (closeAppToast != null) {
            closeAppToast.cancel();
            Log.d(TAG, "Activity termination requested.");
            finish();
        } else {
            closeAppToast = Toast.makeText(this, R.string.backPressedHintText, Toast.LENGTH_SHORT);
            closeAppToast.show();
            resetCloseAppToast();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED, sticky = true)
    public void onEvent(Events.ServiceControlEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        if (event.getServiceState().equals(Events.ServiceControlEvent.ServiceState.START)) {
            FlowConfiguration flowConfiguration = new FlowConfiguration.Builder()
                    .addDigitalOutputPin(FlowManager.PinConfiguration.LEFT_SERVO)
                    .addDigitalOutputPin(FlowManager.PinConfiguration.RIGHT_SERVO)
                    .addDigitalInputPin(FlowManager.PinConfiguration.TOUCH_SIDE_RIGHT)
                    .getConfiguration();

            controlServiceManager.startService(flowConfiguration);
        } else if (event.getServiceState().equals(Events.ServiceControlEvent.ServiceState.STOP)) {
            controlServiceManager.stopService();
        }
    }

    @UiThread(delay = 2000)
    void resetCloseAppToast() {
        closeAppToast = null;
    }

    private void setFragment(final Fragment fragment, final String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, fragmentTag)
                .commit();
    }
}