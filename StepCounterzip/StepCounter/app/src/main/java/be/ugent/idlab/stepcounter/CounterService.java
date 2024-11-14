package be.ugent.idlab.stepcounter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;

public class CounterService extends Service implements SensorEventListener {
    private static final String LOG_TAG = "CounterService";

    private SensorManager mSensorManager;
    private LocalBroadcastManager mBroadcastManager;
    private AlarmManager mAlarmManager;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "--- onCreate ---");
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        initializeDetector();

    }

    private void initializeDetector() {
        Log.d(LOG_TAG, "--- initDetector ---");
        mSteps = 0;
        mDetector = new DummyDetector();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "--- onDestroy ---");
        super.onDestroy();
    }


    // Service interface

    public static boolean RUNNING = false;

    public static final String ACTION_START = "action.start";
    public static final String ACTION_STOP = "action.stop";
    public static final String ACTION_RESET = "action.reset";

    public static final String BROADCAST_DATA = "broadcast.data";
    public static final String BROADCAST_STEPS = "broadcast.stepcount";

    private static final int ONGOING_NOTIFICATION_ID = 42;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "--- onStartCommand ---");

        if (intent.getAction() == null) {
            Log.w(LOG_TAG, "---received intent without action ---");
        } else if (intent.getAction().equals(ACTION_START)) {
            if (!RUNNING) {
                int delay = intent.getIntExtra("delay", SensorManager.SENSOR_DELAY_GAME);
                doStartService(delay);
                RUNNING = true;
            }
        } else if (intent.getAction().equals(ACTION_STOP)) {
            if (RUNNING) {
                doStopService();
                RUNNING = false;
                // FIXME: does this cause service onCreate upon next start?
                //        if so, this resets the step count.
                //        document that.
            }
        } else if (intent.getAction().equals(ACTION_RESET)) {
            initializeDetector();
            broadcastSteps(mSteps);
        } /*else if (intent.getAction().equals(ALARM_SERVICE)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 30);


            mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    calendar.getTimeInMillis(), intent);
            initializeDetector();
            broadcastSteps(mSteps);

        }*/

        // If the system kills the service after onStartCommand() returns, recreate the service
        // and call onStartCommand(), but do not redeliver the last intent.
        //
        // START_STICKY is used for services that are explicitly started and stopped as needed
        return Service.START_STICKY;

        // reseten van de app



    }
    // app laten werken als het nog niet begonnen is


    //reset om 12 u snachts




    // We don't use a local binder, everything goes through intents
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "--- onBind ---");
        return null;
    }

    private void doStartService(int delay) {
        Log.d(LOG_TAG, String.format("--- doStartService(delay=%d) ---", delay));

        // https://developer.android.com/guide/components/services.html#Foreground

        Intent notificationIntent = new Intent(this, CounterService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

        // TODO: clicking this intent should launch the activity, but that doesn't work.

        /*Notification notification =
                new Notification.Builder(this)
                        .setContentTitle("StappenTeller")
                        .setContentText("Stappen worden geteld")
                        //.setSmallIcon(R.mipmap.ic_directions_run)
                        .setContentIntent(pendingIntent)
                        .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
*/
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                delay);
    }

    private void doStopService() {
        Log.d(LOG_TAG, "--- doStopService ---");
        mSensorManager.unregisterListener(this);
        stopForeground(true);
        stopSelf();
    }

    private void broadcastData(long timestamp, float x, float y, float z) {
        Intent intent = new Intent(BROADCAST_DATA)
                .putExtra("timestamp", timestamp)
                .putExtra("x", x)
                .putExtra("y", y)
                .putExtra("z", z);
        mBroadcastManager.sendBroadcast(intent);
    }

    private void broadcastSteps(int count) {

        Intent intent = new Intent(BROADCAST_STEPS).putExtra("count", count);
        mBroadcastManager.sendBroadcast(intent);
    }


    // SensorEventListener interface

    private IDetector mDetector;
    private int mSteps;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            broadcastData(event.timestamp, event.values[0], event.values[1], event.values[2]);
            mDetector.addAccelerationData(event.timestamp, event.values[0], event.values[1],
                    event.values[2], new DetectorLog());

            int steps = mDetector.getSteps();
            if (steps != mSteps) {
                mSteps = steps;
                broadcastSteps(steps);
            }
        }
    }
}

