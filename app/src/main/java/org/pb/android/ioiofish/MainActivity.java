package org.pb.android.ioiofish;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.pb.android.ioiofish.flow.FlowConfiguration;
import org.pb.android.ioiofish.flow.FlowManager;
import org.pb.android.ioiofish.fragment.CalibrateFragment;
import org.pb.android.ioiofish.fragment.CalibrateFragment_;
import org.pb.android.ioiofish.service.ControlServiceManager;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bean
    ControlServiceManager controlServiceManager;

    private Toast closeAppToast = null;

    @AfterViews
    public void initViews() {
        //InfoFragment infoFragment = InfoFragment_.builder().build();
        //setFragment(infoFragment, InfoFragment.TAG);

        CalibrateFragment calibrateFragment = CalibrateFragment_.builder().build();
        setFragment(calibrateFragment, CalibrateFragment.TAG);
    }

    @Override
    public void onResume() {
        super.onResume();

        FlowConfiguration flowConfiguration = new FlowConfiguration.Builder()
                .addDigitalOutputPin(FlowManager.PinConfiguration.LEFT_SERVO)
                .addDigitalOutputPin(FlowManager.PinConfiguration.RIGHT_SERVO)
                .getConfiguration();

        controlServiceManager.startService(flowConfiguration);
    }

    @Override
    public void onPause() {
        controlServiceManager.stopService();
        super.onPause();
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