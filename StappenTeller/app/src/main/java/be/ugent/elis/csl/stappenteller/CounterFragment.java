package be.ugent.elis.csl.stappenteller;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CounterFragment extends Fragment {
    private static final String LOG_TAG = "CounterFragment";


    // Fragment interface

    private TextView mTxtSteps;
    private ToggleButton mBtnEnable;

    private LocalBroadcastManager mBroadcastManager;
    private BroadcastReceiver mStepReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_counter, container, false);

        mTxtSteps = view.findViewById(R.id.txtEntries);

        // functionality for reset button
        Button btnReset = view.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CounterService.class);
                intent.setAction(CounterService.ACTION_RESET);
                getContext().startService(intent);
            }
        });

        // receivers for broadcast events
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer steps = intent.getIntExtra("count", 0);
                mTxtSteps.setText(String.format("%d steps", steps));
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
        Log.d(LOG_TAG, "onDestroy");
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
        Log.d(LOG_TAG, "onStart");
        super.onStart();

        // subscribe to step events
        mBroadcastManager.registerReceiver(mStepReceiver, new IntentFilter(CounterService
                .BROADCAST_STEPS));
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();

        // unsubscribe from step events
        mBroadcastManager.unregisterReceiver(mStepReceiver);
    }
}
