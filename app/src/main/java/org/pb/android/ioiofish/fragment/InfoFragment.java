package org.pb.android.ioiofish.fragment;

import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.event.Events;
import org.pb.android.ioiofish.flow.FlowManager;
import org.pb.android.ioiofish.ui.BalanceView;
import org.pb.android.ioiofish.ui.SignalLevelView;

import java.util.HashMap;
import java.util.Map;

@EFragment(R.layout.fragment_info)
public class InfoFragment extends Fragment {

    public static final String TAG = InfoFragment.class.getSimpleName();

    @ViewById(R.id.ivPluggedState)
    ImageView ivPluggedState;

    @ViewById(R.id.balanceView)
    BalanceView balanceView;

    @ViewById(R.id.signal1)
    SignalLevelView signalLevelView1;

    @ViewById(R.id.signal2)
    SignalLevelView signalLevelView2;

    @ViewById(R.id.signal3)
    SignalLevelView signalLevelView3;

    @ViewById(R.id.signal4)
    SignalLevelView signalLevelView4;

    @ViewById(R.id.signal5)
    SignalLevelView signalLevelView5;

    @ViewById(R.id.signal6)
    SignalLevelView signalLevelView6;

    private Map<Integer, SignalLevelView> signalLevelViewMap = new HashMap<>();

    @AfterViews
    public void initViews() {
        PackageManager packageManager = requireContext().getPackageManager();
        boolean hasAccessorySupport = packageManager.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY);

        Log.v(TAG, "usb accessory support: " + hasAccessorySupport);

        if (!hasAccessorySupport) {
            Toast.makeText(getContext(), "This device does not support usb accessories.", Toast.LENGTH_LONG).show();
        }

        setupSignalLevelViewMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.RotationChangedEvent event) {
        balanceView.update(event.getPitch());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(Events.PluggedStateChangedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        if (event.isPlugged()) {
            ivPluggedState.setImageResource(R.drawable.ic_plugged);
        } else {
            ivPluggedState.setImageResource(R.drawable.ic_unplugged);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEvent(Events.SignalLevelReceivedEvent event) {
        SignalLevelView signalLevelView = signalLevelViewMap.get(event.getPinNumber());
        if (signalLevelView != null) {
            signalLevelView.setSignal();
        }
    }

    private void setupSignalLevelViewMap() {
        signalLevelViewMap.put(FlowManager.PinConfiguration.TOUCH_FRONT_LEFT.pin, signalLevelView1);
        signalLevelViewMap.put(FlowManager.PinConfiguration.TOUCH_FRONT_RIGHT.pin, signalLevelView5);
        signalLevelViewMap.put(FlowManager.PinConfiguration.TOUCH_FRONT_TOP.pin, signalLevelView3);
        signalLevelViewMap.put(FlowManager.PinConfiguration.TOUCH_FRONT_BOTTOM.pin, signalLevelView4);
        signalLevelViewMap.put(FlowManager.PinConfiguration.TOUCH_SIDE_LEFT.pin, signalLevelView2);
        signalLevelViewMap.put(FlowManager.PinConfiguration.TOUCH_SIDE_RIGHT.pin, signalLevelView6);
    }
}
