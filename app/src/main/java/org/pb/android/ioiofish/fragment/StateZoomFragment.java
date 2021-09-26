package org.pb.android.ioiofish.fragment;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.ui.BaseStateView;

@EFragment(R.layout.fragment_state_zoom)
public class StateZoomFragment extends Fragment {

    public static final String TAG = StateZoomFragment.class.getSimpleName();

    @ViewById(R.id.baseStateView)
    BaseStateView baseStateView;

    @AfterViews
    public void initViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Click(R.id.baseStateView)
    public void onBaseStateViewClicked() {
        if (baseStateView.isConnected) {
            baseStateView.setDisconnected();
        } else {
            baseStateView.setConnected();
        }
    }
}
