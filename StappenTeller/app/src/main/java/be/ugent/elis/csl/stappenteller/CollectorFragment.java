package be.ugent.elis.csl.stappenteller;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.opencsv.CSVWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

// TODO: this fragment is stateful and performs its own I/O, complicating instance management.
//       refactor it to only contain UI code, and put the rest in a service or in the main activity.

public class CollectorFragment extends Fragment {
    private static final String LOG_TAG = "CollectorFragment";


    // Fragment interface

    private TextView mTxtEntries;
    private ToggleButton mBtnEnable;
    private Spinner mSpinDelay;

    private LocalBroadcastManager mBroadcastManager;
    private BroadcastReceiver mStepReceiver;

    private final static int CREATE_REQUEST_CODE = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        // our fragment has global state, so make sure its instances don't get re-created
        // upon e.g. rotation, screen on-off, etc
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_collector, container, false);

        mTxtEntries = view.findViewById(R.id.txtEntries);

        // functionality for enable toggle button
        mBtnEnable = view.findViewById(R.id.btnEnable);
        mBtnEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    // NOTE: doSelectFile has side-effects, so make sure we don't call it when
                    //       restoring the view after e.g. a rotation
                    if (isChecked)
                        doSelectFile();
                    else if (COLLECTING)
                        doStopCollect();
                }
            }
        });

        // populate the sensor delay spinner
        // TODO: entries attribute in layout?
        mSpinDelay = (Spinner) view.findViewById(R.id.spinDelay);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.sensor_delay_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinDelay.setAdapter(adapter);

        // receivers for broadcast events
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long timestamp = intent.getLongExtra("timestamp", 0);
                float x = intent.getFloatExtra("x", 0f);
                float y = intent.getFloatExtra("y", 0f);
                float z = intent.getFloatExtra("z", 0f);
                onCollectedData(timestamp, x, y, z);
            }
        };

        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();

        if (COLLECTING && isRemoving())
            doStopCollect();
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();

        mSpinDelay.setEnabled(!COLLECTING);

        // subscribe to step events
        mBroadcastManager.registerReceiver(mStepReceiver, new IntentFilter(CounterService
                .BROADCAST_DATA));
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();

        mBroadcastManager.unregisterReceiver(mStepReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
                onSelectedFile(data.getData());
            else
                mBtnEnable.setChecked(false);
        }
    }


    // UI state-machine

    private boolean COLLECTING = false;

    private CSVWriter mWriter;
    private int mEntries;

    private void doSelectFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("text/csv")
                .putExtra(Intent.EXTRA_TITLE, "output.csv");
        startActivityForResult(intent, CREATE_REQUEST_CODE);
    }

    private void onSelectedFile(Uri uri) {
        if (!doStartCollect(uri))
            mBtnEnable.setChecked(false);
    }

    private boolean doStartCollect(Uri uri) {
        assert (!COLLECTING);

        // get the output stream
        OutputStream outputStream;
        try {
            outputStream = getContext().getContentResolver().openOutputStream(uri, "w");
        } catch (FileNotFoundException e) {
            showError(String.format("Could not open file: %s", e.getMessage()));
            return false;
        }

        // encapsulate in a writer
        OutputStreamWriter outputWriter;
        try {
            outputWriter = new OutputStreamWriter(outputStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            showError(String.format("Could not open file: %s", e.getMessage()));
            return false;
        }

        // create a CSV writer
        mWriter = new CSVWriter(outputWriter);
        String[] header = {"timestamp", "x", "y", "z"};
        mWriter.writeNext(header);

        // start the service
        Intent intent = new Intent(getContext(), CounterService.class)
                .setAction(CounterService.ACTION_START)
                .putExtra("delay", mSpinDelay.getSelectedItemPosition());
        getContext().startService(intent);

        mSpinDelay.setEnabled(false);
        COLLECTING = true;
        return true;
    }

    private void onCollectedData(long timestamp, float x, float y, float z) {
        if (COLLECTING) {
            mEntries += 1;
            mTxtEntries.setText(String.format("%d entries", mEntries));

            String[] entries = {String.valueOf(timestamp), String.valueOf(x), String.valueOf(y),
                    String.valueOf(z)};
            mWriter.writeNext(entries);
        }
    }

    private boolean doStopCollect() {
        assert (COLLECTING);

        Intent intent = new Intent(getContext(), CounterService.class);
        intent.setAction(CounterService.ACTION_STOP);
        getContext().startService(intent);

        try {
            mWriter.close();
        } catch (IOException e) {
            showError(String.format("Could not write data: %s", e.getMessage()));
            mBtnEnable.setChecked(false);
            return false;
        } finally {
            mSpinDelay.setEnabled(true);
            COLLECTING = false;
            mWriter = null;
            mEntries = 0;
        }

        return true;
    }


    // Helpers

    private void showError(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
