package be.ugent.elis.csl.stappenteller;

public interface IDetector {
    public void addAccelerationData(long timestamp, float x, float y, float z, DetectorLog log);

    public int getSteps();
}
