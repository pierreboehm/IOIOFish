package org.pb.android.ioiofish.fragment;

import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.event.Events;
import org.pb.android.ioiofish.ui.BalanceView;
import org.pb.android.ioiofish.ui.SignalLevelView;

@EFragment(R.layout.fragment_info)
public class InfoFragment extends Fragment {

    public static final String TAG = InfoFragment.class.getSimpleName();

    @ViewById(R.id.ivPluggedState)
    ImageView ivPluggedState;

    @ViewById(R.id.balanceView)
    BalanceView balanceView;

    @ViewById(R.id.signal6)
    SignalLevelView signalLevelView6;

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

    @Click(R.id.ivPluggedState)
    public void onPluggedStateViewClicked() {
        signalLevelView6.setSignal();
    }
}
