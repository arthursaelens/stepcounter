package be.ugent.idlab.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class HomeFragment extends Fragment {
    private static final String LOG_TAG = "HomeFragment";
    private TextView mSteps_1;
    private LocalBroadcastManager mBroadcastManager;
    private BroadcastReceiver mStepReceiver;

    public HomeFragment() {
        // Required empty public constructor
        Log.d(LOG_TAG, "--- in constructor ---");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(LOG_TAG, "--- in onCreate ---");

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString("ARG_PARAM1");
            mParam2 = getArguments().getString("ARG_PARAM2"");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "--- in onCreateView ---");
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mSteps_1 = view.findViewById(R.id.Stappen);

        // receivers for broadcast events
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer steps = intent.getIntExtra("count", 0);
                mSteps_1.setText(String.format("%d", steps));
            }
        };

        // start the service
        Intent intent = new Intent(getContext(), CounterService.class);
        intent.setAction(CounterService.ACTION_START);
        getContext().startService(intent);

        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "--- onDestroy ---");
        super.onDestroy();

        if (isRemoving()) {
            // stop the service
            Intent intent = new Intent(getContext(), CounterService.class);
            intent.setAction(CounterService.ACTION_STOP);
            getContext().startService(intent);
        }
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "--- onStart ---");
        super.onStart();

        // subscribe to step events
        mBroadcastManager.registerReceiver(mStepReceiver, new IntentFilter(CounterService
                .BROADCAST_STEPS));
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "--- onStop ---");
        super.onStop();

        // unsubscribe from step events
        mBroadcastManager.unregisterReceiver(mStepReceiver);
    }
}