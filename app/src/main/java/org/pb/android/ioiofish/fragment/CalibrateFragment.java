package org.pb.android.ioiofish.fragment;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.event.Events;
import org.pb.android.ioiofish.flow.FlowManager;

@EFragment(R.layout.fragment_calibrate)
public class CalibrateFragment extends Fragment {

    public static final String TAG = CalibrateFragment.class.getSimpleName();

    @Bean
    FlowManager flowManager;

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
    public void onEvent(Events.ValueChangedEvent event) {

    }
}
