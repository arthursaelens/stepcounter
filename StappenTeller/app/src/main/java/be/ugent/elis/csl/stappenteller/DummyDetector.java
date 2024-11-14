package be.ugent.elis.csl.stappenteller;

import java.lang.Math;
import java.util.ArrayList;

public class DummyDetector implements IDetector {
    private int mSteps = 0;
    private int vorige = 0;

    public void addAccelerationData(long timestamp, float x, float y, float z, DetectorLog log) {
        ArrayList<Float> lijst_x = new ArrayList<Float>();
        lijst_x.add(x);
        ArrayList<Float> lijst_y = new ArrayList<Float>();
        lijst_y.add(y);
        ArrayList<Float> lijst_z = new ArrayList<Float>();
        lijst_z.add(z);

        if (lijst_x.size() > 50){
            lijst_x.remove(49)
        }
        if (lijst_y.size() > 50){
            lijst_y.remove(49)
        }
        if (lijst_z.size() > 50){
            lijst_z.remove(49)
        }

        double a = =0.1*(java.lang.Math.sqrt((0.1* lijst_x.get(0) +0.2*lijst_x.get(1)+0.4*lijst_x.get(2)+0.2*lijst_x.get(3)+0.1*lijst_x.get(4))*(0.1*lijst_x.get(0)+0.2*lijst_x.get(1)+0.4*lijst_x.get(2)+0.2*lijst_x.get(3)+0.1*lijst_x.get(4))+(0.1*lijst_y.get(0)+0.2*lijst_y.get(1)+0.4*lijst_y.get(2)+0.2*lijst_y.get(3)+0.1*lijst_y.get(4))*(0.1*lijst_y.get(0)+0.2*lijst_y.get(1)+0.4*lijst_y.get(2)+0.2*lijst_y.get(3)+0.1*lijst_y.get(4))+(0.1*lijst_z.get(0)+0.2*lijst_z.get(1)+0.4*lijst_z.get(2)+0.2*lijst_z.get(3)+0.1*lijst_z.get(4))*(0.1*lijst_z.get(0)+0.2*lijst_z.get(1)+0.4*lijst_z.get(2)+0.2*lijst_z.get(3)+0.1*lijst_z.get(4))))+0.1*(java.lang.Math.sqrt((0.1*lijst_x.get(1)+0.2*lijst_x.get(2)+0.4*lijst_x.get(3)+0.2*lijst_x.get(4)+0.1*lijst_x.get(5))*(0.1*lijst_x.get(1)+0.2*lijst_x.get(2)+0.4*lijst_x.get(3)+0.2*lijst_x.get(4)+0.1*lijst_x.get(5))+(0.1*lijst_y.get(1)+0.2*lijst_y.get(2)+0.4*lijst_y.get(3)+0.2*lijst_y.get(4)+0.1*lijst_y.get(5))*(0.1*lijst_y.get(1)+0.2*lijst_y.get(2)+0.4*lijst_y.get(3)+0.2*lijst_y.get(4)+0.1*lijst_y.get(5))+(0.1*lijst_z.get(1)+0.2*lijst_z.get(2)+0.4*lijst_z.get(3)+0.2*lijst_z.get(4)+0.1*lijst_z.get(5))*(0.1*lijst_z.get(1)+0.2*lijst_z.get(2)+0.4*lijst_z.get(3)+0.2*lijst_z.get(4)+0.1*lijst_z.get(5))))+0.3*(java.lang.Math.sqrt((0.1*lijst_x.get(2)+0.2*lijst_x.get(3)+0.4*lijst_x.get(4)+0.2*lijst_x.get(5)0.1*lijst_x.get(6))*(0.1*lijst_x.get(2)+0.2*lijst_x.get(3)+0.4*lijst_x.get(4)+0.2*lijst_x.get(5)0.1*lijst_x.get(6))+(0.1*lijst_y.get(2)+0.2*lijst_y.get(3)+0.4*lijst_y.get(4)+0.2*lijst_y.get(5)+0.1*lijst_y.get(6))*(0.1*lijst_y.get(2)+0.2*lijst_y.get(3)+0.4*lijst_y.get(4)+0.2*lijst_y.get(5)+0.1*lijst_y.get(6))+(0.1*lijst_z.get(2)+0.2*lijst_z.get(3)+0.4*lijst_z.get(4)+0.2*lijst_z.get(5)+0.1*lijst_z.get(6))*(0.1*lijst_z.get(2)+0.2*lijst_z.get(3)+0.4*lijst_z.get(4)+0.2*lijst_z.get(5)+0.1*lijst_z.get(6))))+0.3*(java.lang.Math.sqrt((0.1*lijst_x.get(3)+0.2*lijst_x.get(4)+0.4*lijst_x.get(5)+0.2*lijst_x.get(6)+0.1*lijst_x.get(7))*(0.1*lijst_x.get(3)+0.2*lijst_x.get(4)+0.4*lijst_x.get(5)+0.2*lijst_x.get(6)+0.1*lijst_x.get(7))+(0.1*lijst_y.get(3)+0.2*lijst_y.get(4)+0.4*lijst_y.get(5)+0.2*lijst_y.get(6)+0.1*lijst_y.get(7))*(0.1*lijst_y.get(3)+0.2*lijst_y.get(4)+0.4*lijst_y.get(5)+0.2*lijst_y.get(6)+0.1*lijst_y.get(7))+(0.1*lijst_z.get(3)+0.2*lijst_z.get(4)+0.4*lijst_z.get(5)+0.2*lijst_z.get(6)+0.1*lijst_z.get(7))*(0.1*lijst_z.get(3)+0.2*lijst_z.get(4)+0.4*lijst_z.get(5)+0.2*lijst_z.get(6)+0.1*lijst_z.get(7))))+0.2*(java.lang.Math.sqrt((0.1*lijst_x.get(4)+0.2*lijst_x.get(5)+0.4*lijst_x.get(6)+0.2*lijst_x.get(7)+0.1*lijst_x.get(8))*(0.1*lijst_x.get(4)+0.2*lijst_x.get(5)+0.4*lijst_x.get(6)+0.2*lijst_x.get(7)+0.1*lijst_x.get(8))+(0.1*lijst_y.get(4)+0.2*lijst_y.get(5)+0.4*lijst_y.get(6)+0.2*lijst_y.get(7)+0.1*lijst_y.get(8))*(0.1*lijst_y.get(4)+0.2*lijst_y.get(5)+0.4*lijst_y.get(6)+0.2*lijst_y.get(7)+0.1*lijst_y.get(8))+(0.1*lijst_z.get(4)+0.2*lijst_z.get(5)+0.4*lijst_z.get(6)+0.2*lijst_z.get(7)+0.1*lijst_z.get(8))*(0.1*lijst_z.get(4)+0.2*lijst_z.get(5)+0.4*lijst_z.get(6)+0.2*lijst_z.get(7)+0.1*lijst_z.get(8))))-9.81;

        if (vorige == 0) {
            if (a >= 0) {
                mSteps += 1;
                vorige = 1;
            }
        }
        else {
            if (a <= 0) {
                vorige = 0;
            }
        }

    }

    public int getSteps() {
        return mSteps;
    }
}
