package org.pb.android.ioiofish.fragment;

import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.event.Events;

@EFragment(R.layout.fragment_info)
public class InfoFragment extends Fragment {

    public static final String TAG = InfoFragment.class.getSimpleName();

    @ViewById(R.id.tvRotationInfo)
    TextView rotationInfo;

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
        String eventInfo = String.format("%d° %d° %d°", (int) event.getAzimut(), (int) event.getPitch(), (int) event.getRoll());
        rotationInfo.setText(eventInfo);
    }
}
