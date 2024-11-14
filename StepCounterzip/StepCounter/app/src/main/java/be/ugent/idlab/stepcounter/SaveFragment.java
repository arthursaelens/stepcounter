package be.ugent.idlab.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
// TODO: this fragment is stateful and performs its own I/O, complicating instance management.
//       refactor it to only contain UI code, and put the rest in a service or in the main activity.


public class SaveFragment extends Fragment {
    private static final String LOG_TAG = "SaveFragment";
    public static final String BROADCAST_DATA = "broadcast.data";
    public static final String BROADCAST_STEPS = "broadcast.stepcount";
    private EditText etFileName;
    private Spinner spinDelay;
    private ToggleButton tbStartStop;
    private TextView tvSteps;
    private Button btSave;
    private LocalBroadcastManager mBroadcastManager;
    private BroadcastReceiver mStepReceiver;
    private CSVWriter csvWriter;
    private List<String[]> entries;


    public SaveFragment() {
        Log.d(LOG_TAG, "--- in constructor ---");
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "--- in onCreate ---");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "--- in onCreateView ---");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        //retrieve widgets
        etFileName = view.findViewById(R.id.save_et_filename);
        spinDelay = view.findViewById(R.id.save_spin_delay);
        tbStartStop = view.findViewById(R.id.save_tb_startstop);
        tvSteps = view.findViewById(R.id.save_tv_steps);
        btSave = view.findViewById(R.id.save_bt_save);

        //enable tbstartstop if filename provided (might further be improved with filename checks)
        etFileName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    // action on done
                    tbStartStop.setEnabled(true);
                }
                return false;
            }
        });

        // selecting the speed (==> load values from sensor_delay_array (string.xml) within spinner)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.sensor_delay_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDelay.setAdapter(adapter);


        // functionality for start-stop toggle button
        tbStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CounterService.class);
                if (tbStartStop.isChecked()){
                    Log.d(LOG_TAG, "--- onlick START---");
                    entries = new ArrayList<String[]>();
                    intent.setAction(CounterService.ACTION_START);
                    intent.putExtra("delay",spinDelay.getSelectedItemPosition());
                    btSave.setEnabled(false);
                } else {
                    Log.d(LOG_TAG, "--- onlick STOP---");
                    btSave.setEnabled(true);
                    intent.setAction(CounterService.ACTION_STOP);
                }
                getContext().startService(intent);
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                etFileName.setText("");
                etFileName.setHint("Provide filename");
                tbStartStop.setEnabled(false);
                btSave.setEnabled(false);
                tvSteps.setText("0 steps");
            }
        });
        // receivers for broadcast events
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        /*mStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (tbStartStop.isChecked()){
                    Integer steps = intent.getIntExtra("count", 0);
                    tvSteps.setText(String.format("%d steps", steps));

                }
            }
        };*/

        mStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_DATA) && entries!=null){

                    long timestamp = intent.getLongExtra("timestamp", 0);
                    float x = intent.getFloatExtra("x", 0f);
                    float y = intent.getFloatExtra("y", 0f);
                    float z = intent.getFloatExtra("z", 0f);
                    String[] entry = {String.valueOf(timestamp), String.valueOf(x), String.valueOf(y),
                            String.valueOf(z)};
                    entries.add(entry);
                } else if (intent.getAction().equals(BROADCAST_STEPS)){
                    if (tbStartStop.isChecked()){
                        Integer steps = intent.getIntExtra("count", 0);
                        tvSteps.setText(String.format("%d steps", steps));
                    }
                }
            }
        };

        return view;
    }

    private boolean saveData(){
        // get the output stream
        OutputStream outputStream;
        try {

            File dir = getContext().getFilesDir();
            File file = new File(dir, String.valueOf(etFileName.getText()));
            Uri uri = Uri.fromFile(file);
            outputStream = getContext().getContentResolver().openOutputStream(uri, "wt");
        } catch (Exception e) {
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
        csvWriter = new CSVWriter(outputWriter);
        String[] header = {"timestamp", "x", "y", "z"};
        csvWriter.writeNext(header);
        for(int i=0;i< entries.size();i++){
            csvWriter.writeNext((String[])entries.get(i));
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            showError(String.format("Could not write data: %s", e.getMessage()));
            return false;
        } finally {
            csvWriter = null;
        }
        entries = null;
        Toast.makeText(this.getContext(), etFileName.getText()+" succesfully saved!", Toast.LENGTH_LONG).show();
        return true;
    }

    private void showError(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "--- onDestroy ---");
        super.onDestroy();
        /*if (isRemoving()) {
            // stop the service
            Intent intent = new Intent(getContext(), CounterService.class);
            intent.setAction(CounterService.ACTION_STOP);
            getContext().startService(intent);
        }*/
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "--- onStart ---");
        super.onStart();

        // subscribe to step events
        mBroadcastManager.registerReceiver(mStepReceiver, new IntentFilter(CounterService
                .BROADCAST_STEPS));
        mBroadcastManager.registerReceiver(mStepReceiver, new IntentFilter(CounterService
                .BROADCAST_DATA));

    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "--- onStop ---");
        super.onStop();

        // unsubscribe from step events
        mBroadcastManager.unregisterReceiver(mStepReceiver);
    }
}