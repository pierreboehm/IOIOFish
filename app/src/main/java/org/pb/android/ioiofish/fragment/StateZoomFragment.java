package org.pb.android.ioiofish.fragment;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.event.Events;
import org.pb.android.ioiofish.ui.BaseStateView;

@SuppressLint("NonConstantResourceId")
@EFragment(R.layout.fragment_state_zoom)
public class StateZoomFragment extends Fragment {

    public static final String TAG = StateZoomFragment.class.getSimpleName();

    @ViewById(R.id.baseStateView)
    BaseStateView baseStateView;

    @AfterViews
    public void initViews() {
        //baseStateView.setConnecting();
        EventBus.getDefault().postSticky(new Events.ServiceControlEvent(Events.ServiceControlEvent.ServiceState.START));
        baseStateView.setConnected();
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(Events.PluggedStateChangedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        if (event.isPlugged()) {
            baseStateView.setConnected();
        } else {
            baseStateView.setDisconnected();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.RotationChangedEvent event) {
        //baseStateView.runServos();
        baseStateView.receiveGyroSignal(event.getPitch());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.SignalLevelReceivedEvent event) {
        baseStateView.receiveSensorSignal();
    }

    @Click(R.id.baseStateView)
    public void onBaseStateViewClicked() {
        baseStateView.receiveSensorSignal();
    }
}
