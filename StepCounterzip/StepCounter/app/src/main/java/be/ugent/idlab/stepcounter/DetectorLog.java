package be.ugent.idlab.stepcounter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DetectorLog {
    private Map<String, String> mLog;

    public DetectorLog() {
        mLog = new HashMap<>();
    }

    public void put(String name, String data) { mLog.put(name, data); }
    public void put(String name, float data) { mLog.put(name, Float.toString(data)); }
    public void put(String name, double data) { mLog.put(name, Double.toString(data)); }
    public void put(String name, int data) { mLog.put(name, Integer.toString(data)); }
    public void put(String name, long data) { mLog.put(name, Long.toString(data)); }
    public void put(String name, boolean data) { mLog.put(name, data ? "TRUE" : "FALSE"); }

    public Set<String> keys() { return mLog.keySet(); }

    public Map<String, String> data() { return mLog; }
}
