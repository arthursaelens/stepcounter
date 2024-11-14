package be.ugent.idlab.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;

public class DummyDetector implements IDetector {

    private int mSteps_1 = 0;
    private int vorige_1 = 0;

    private double t = 1.25*9.81;

    private int index;

    ArrayList<Float> lijst_x;
    ArrayList<Float> lijst_y;
    ArrayList<Float> lijst_z;
    ArrayList<Float> lijst_a;

    public DummyDetector() {
        lijst_x = new ArrayList<Float>();
        lijst_y = new ArrayList<Float>();
        lijst_z = new ArrayList<Float>();
        lijst_a = new ArrayList<Float>();
        for (int j = 0; j < 200; j++) {
            lijst_x.add(0.0F);
            lijst_y.add(0.0F);
            lijst_z.add(0.0F);
            lijst_a.add(0.0F);
        }
        index = 0;
    }



    public void addAccelerationData(long timestamp, float x, float y, float z, DetectorLog log) {
        //System.out.println("Steps: "+mSteps+ "hallo");
        // index +1 op het einde anders begint het vanaf 1
        // System.out.println("x:"+x);
        // System.out.println("y:"+y);
        // System.out.println("z:"+z);
        // System.out.println("index: "+index);


        lijst_x.set(index, x);
        lijst_y.set(index, y);
        lijst_z.set(index, z);




        int i = ((getIndex()-9+200) % 200); //kopie van de index gebruikt als index voor het gemiddelde
        int b = (getIndex()-4+200) % 200;// referentie voor de while lus
        float l = 0.0F;
        double a = 0;
        // - 9.81? //System.out.println("i:"+i);

        while(i != b) {
            if (i == (b-5+200)%200 || i == ((b-5+1+200)%200)) {
                l = 0.1F;
            }
            if (i == ((b-5+2+200)%200) || i == (b-5+3+200)%200) {
                l = 0.3F;
            }
            if (i == (b-5+4+200)%200) {
                l = 0.2F;
            }
            //System.out.println("l: "+l);

            a += l * java.lang.Math.sqrt(java.lang.Math.pow((0.1 * lijst_x.get(i) + 0.2 * lijst_x.get((i+1+200)%200) + 0.4 * lijst_x.get((i+2+200)%200) + 0.2 * lijst_x.get((i+3+200)%200) + 0.1 * lijst_x.get((i+4+200)%200)), 2) + java.lang.Math.pow((0.1 * lijst_y.get(i) + 0.2 * lijst_y.get((i+1+200)%200) + 0.4 * lijst_y.get((i+2+200)%200) + 0.2 * lijst_y.get((i+3+200)%200) + 0.1 * lijst_y.get((i+4+200)%200)), 2) + java.lang.Math.pow((0.1 * lijst_z.get(i) + 0.2 * lijst_z.get((i+1+200)%200) + 0.4 * lijst_z.get((i+2+200)%200) + 0.2 * lijst_z.get((i+3+200)%200) + 0.1 * lijst_z.get((i+4+200)%200)), 2));
            //System.out.println("a:"+a);


            // System.out.println("x1:"+lijst_x.get(i));
            // System.out.println("x2:"+lijst_x.get((i+1+200)%200));
            // System.out.println("x3:"+lijst_x.get((i+2+200)%200));
            // System.out.println("x4:"+lijst_x.get((i+3+200)%200));
            // System.out.println("x5:"+lijst_x.get((i+4+200)%200));
            // System.out.println("i:"+i);

            i = (i+1+200) % 200;

            //i+1
            }

        lijst_a.set(index, (float) a);
        //System.out.println("ar: "+a);
        // System.out.printf("i:%d%n", (i + 1 + 200) % 200);
        // System.out.println("index:"+index);
        if (index == 199){
            t = 0.70*Collections.max(lijst_a);
        }
        //System.out.println("t:"+t);
        //private void complexThreshold (double a) {
        // T += 0.75*Collections.max(lijst_a);
        // }

        // a = (java.lang.Math.sqrt((0.1 * lijst_x.get(0) + 0.2 * lijst_x.get(1) + 0.4 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.1 * lijst_x.get(4)) * (0.1 * lijst_x.get(0) + 0.2 * lijst_x.get(1) + 0.4 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.1 * lijst_x.get(4)) + (0.1 * lijst_y.get(0) + 0.2 * lijst_y.get(1) + 0.4 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.1 * lijst_y.get(4)) * (0.1 * lijst_y.get(0) + 0.2 * lijst_y.get(1) + 0.4 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.1 *lijst_y.get(4)) + (0.1 * lijst_z.get(0) + 0.2 * lijst_z.get(1) + 0.4 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.1 * lijst_z.get(4)) * (0.1 * lijst_z.get(0) + 0.2 * lijst_z.get(1) + 0.4 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.1 * lijst_z.get(4)))) + 0.1 * (java.lang.Math.sqrt((0.1 * lijst_x.get(1) + 0.2 * lijst_x.get(2) + 0.4 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.1 * lijst_x.get(5)) * (0.1 * lijst_x.get(1) + 0.2 * lijst_x.get(2) + 0.4 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.1 * lijst_x.get(5)) + (0.1 * lijst_y.get(1) + 0.2 * lijst_y.get(2) + 0.4 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.1 * lijst_y.get(5)) * (0.1 * lijst_y.get(1) + 0.2 * lijst_y.get(2) + 0.4 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.1 * lijst_y.get(5)) + (0.1 * lijst_z.get(1) + 0.2 * lijst_z.get(2) + 0.4 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.1 * lijst_z.get(5)) * (0.1 * lijst_z.get(1) + 0.2 * lijst_z.get(2) + 0.4 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.1 * lijst_z.get(5)))) + 0.3 * (java.lang.Math.sqrt((0.1 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.4 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.1 * lijst_x.get(6)) * (0.1 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.4 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.1 * lijst_x.get(6)) + (0.1 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.4 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.1 * lijst_y.get(6)) * (0.1 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.4 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.1 * lijst_y.get(6)) + (0.1 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.4 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.1 * lijst_z.get(6)) * (0.1 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.4 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.1 * lijst_z.get(6)))) + 0.3 * (java.lang.Math.sqrt((0.1 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.4 * lijst_x.get(5) + 0.2 * lijst_x.get(6) + 0.1 * lijst_x.get(7)) * (0.1 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.4 * lijst_x.get(5) + 0.2 * lijst_x.get(6) + 0.1 * lijst_x.get(7)) + (0.1 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.4 * lijst_y.get(5) + 0.2 * lijst_y.get(6) + 0.1 * lijst_y.get(7)) * (0.1 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.4 * lijst_y.get(5) + 0.2 * lijst_y.get(6) + 0.1 * lijst_y.get(7)) + (0.1 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.4 * lijst_z.get(5) + 0.2 * lijst_z.get(6) + 0.1 * lijst_z.get(7)) * (0.1 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.4 * lijst_z.get(5) + 0.2 * lijst_z.get(6) + 0.1 * lijst_z.get(7)))) + 0.2 * (java.lang.Math.sqrt((0.1 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.4 * lijst_x.get(6) + 0.2 * lijst_x.get(7) + 0.1 * lijst_x.get(8)) * (0.1 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.4 * lijst_x.get(6) + 0.2 * lijst_x.get(7) + 0.1 * lijst_x.get(8)) + (0.1 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.4 * lijst_y.get(6) + 0.2 * lijst_y.get(7) + 0.1 * lijst_y.get(8)) * (0.1 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.4 * lijst_y.get(6) + 0.2 * lijst_y.get(7) + 0.1 * lijst_y.get(8)) + (0.1 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.4 * lijst_z.get(6) + 0.2 * lijst_z.get(7) + 0.1 * lijst_z.get(8)) * (0.1 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.4 * lijst_z.get(6) + 0.2 * lijst_z.get(7) + 0.1 * lijst_z.get(8)))) - 9.81;
        // System.out.println("vorige: "+vorige);
        // System.out.println("a:"+a);

        if (vorige_1 == 0) {

            if ( a > t) {
                mSteps_1 += 1;
                vorige_1 = 1;
            }
        }
        else {
            if ( a < t) {
                vorige_1 = 0;
            }
        }
        //System.out.println("============================ ");

    index = (index + 1) % 200;
    }


    public int getSteps() {
        return mSteps_1;
    }

    public int getIndex() {
        return index;
    }


}