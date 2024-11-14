package be.ugent.idlab.stepcounter;

import java.util.ArrayList;
import java.util.Collections;

public class DummyDetector implements IDetector {

    private int mSteps_1 = 0;
    //private int mSteps_2 = 0;
    private int vorige_1 = 0;
    //private int vorige_2 = 0;

    private int index;
    private double drempel;


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
        // plaats in de lijst waar we de waarden gaan in toevoegen
        drempel = 12.2625; //1,25*9,81
    }



    public void addAccelerationData(long timestamp, float x, float y, float z, DetectorLog log) {
        //System.out.println("Steps: "+mSteps+ "hallo");
        // index op het einde anders begint het vanaf 1


        //System.out.println("index: "+index);


        lijst_x.set(index, x);
        lijst_y.set(index, y);
        lijst_z.set(index, z);
        // voegen de x-,y- en z-waarden in in onze lijsten



        int i = ((getIndex()-9+200) % 200); //kopie van de index gebruikt als index voor het gemiddelde
        int b = (getIndex()-4+200) % 200;// referentie voor de while lus
        float l = 0.0F;
        double a = 0;
        //System.out.println("i:"+i);

        while(i != b) {
            if (i == (b-5+200)%200 || i == ((b-4+200)%200)) {
                l = 0.1F;
            }
            if (i == ((b-3+200)%200) || i == (b-2+200)%200) {
                l = 0.3F;
            }
            if (i == (b-1+200)%200) {
                l = 0.2F;
            }
            // gewogen gemiddelde na het 1D maken met de coefficienten 0.1,0.1,0.3,0.3,0.2

            System.out.println("l: "+l);

            a += l * java.lang.Math.sqrt(java.lang.Math.pow((0.1 * lijst_x.get(i) + 0.2 * lijst_x.get((i+1+200)%200) + 0.4 * lijst_x.get((i+2+200)%200) + 0.2 * lijst_x.get((i+3+200)%200) + 0.1 * lijst_x.get((i+4+200)%200)), 2) + java.lang.Math.pow((0.1 * lijst_y.get(i) + 0.2 * lijst_y.get((i+1+200)%200) + 0.4 * lijst_y.get((i+2+200)%200) + 0.2 * lijst_y.get((i+3+200)%200) + 0.1 * lijst_y.get((i+4+200)%200)), 2) + java.lang.Math.pow((0.1 * lijst_z.get(i) + 0.2 * lijst_z.get((i+1+200)%200) + 0.4 * lijst_z.get((i+2+200)%200) + 0.2 * lijst_z.get((i+3+200)%200) + 0.1 * lijst_z.get((i+4+200)%200)), 2));
            // gewogen gemiddelde voor het 1D maken met coefficienten 0.1,0.2,0.4,0.2,0.1

            lijst_a.set(index, (float) a);
            //deze waarden moeten ook onthouden worden voor dan onzer complexe drempelwaarde

            //System.out.println("a: "+a);

            i = (i+1+200) % 200;


            //i+1
        }

        if (index == 199){
            drempel = 0.65*Collections.max(lijst_a);
        }
        //complexe drempelwaarde voor als de gebruiker begint te sprinten of lopen
        if (drempel < 12.2625){
           drempel = 12.2625;
        }
        // minimale drempelwaarde voor als de gebruiker stil zit


        //a = (java.lang.Math.sqrt((0.1 * lijst_x.get(0) + 0.2 * lijst_x.get(1) + 0.4 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.1 * lijst_x.get(4)) * (0.1 * lijst_x.get(0) + 0.2 * lijst_x.get(1) + 0.4 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.1 * lijst_x.get(4)) + (0.1 * lijst_y.get(0) + 0.2 * lijst_y.get(1) + 0.4 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.1 * lijst_y.get(4)) * (0.1 * lijst_y.get(0) + 0.2 * lijst_y.get(1) + 0.4 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.1 * lijst_y.get(4)) + (0.1 * lijst_z.get(0) + 0.2 * lijst_z.get(1) + 0.4 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.1 * lijst_z.get(4)) * (0.1 * lijst_z.get(0) + 0.2 * lijst_z.get(1) + 0.4 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.1 * lijst_z.get(4)))) + 0.1 * (java.lang.Math.sqrt((0.1 * lijst_x.get(1) + 0.2 * lijst_x.get(2) + 0.4 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.1 * lijst_x.get(5)) * (0.1 * lijst_x.get(1) + 0.2 * lijst_x.get(2) + 0.4 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.1 * lijst_x.get(5)) + (0.1 * lijst_y.get(1) + 0.2 * lijst_y.get(2) + 0.4 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.1 * lijst_y.get(5)) * (0.1 * lijst_y.get(1) + 0.2 * lijst_y.get(2) + 0.4 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.1 * lijst_y.get(5)) + (0.1 * lijst_z.get(1) + 0.2 * lijst_z.get(2) + 0.4 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.1 * lijst_z.get(5)) * (0.1 * lijst_z.get(1) + 0.2 * lijst_z.get(2) + 0.4 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.1 * lijst_z.get(5)))) + 0.3 * (java.lang.Math.sqrt((0.1 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.4 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.1 * lijst_x.get(6)) * (0.1 * lijst_x.get(2) + 0.2 * lijst_x.get(3) + 0.4 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.1 * lijst_x.get(6)) + (0.1 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.4 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.1 * lijst_y.get(6)) * (0.1 * lijst_y.get(2) + 0.2 * lijst_y.get(3) + 0.4 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.1 * lijst_y.get(6)) + (0.1 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.4 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.1 * lijst_z.get(6)) * (0.1 * lijst_z.get(2) + 0.2 * lijst_z.get(3) + 0.4 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.1 * lijst_z.get(6)))) + 0.3 * (java.lang.Math.sqrt((0.1 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.4 * lijst_x.get(5) + 0.2 * lijst_x.get(6) + 0.1 * lijst_x.get(7)) * (0.1 * lijst_x.get(3) + 0.2 * lijst_x.get(4) + 0.4 * lijst_x.get(5) + 0.2 * lijst_x.get(6) + 0.1 * lijst_x.get(7)) + (0.1 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.4 * lijst_y.get(5) + 0.2 * lijst_y.get(6) + 0.1 * lijst_y.get(7)) * (0.1 * lijst_y.get(3) + 0.2 * lijst_y.get(4) + 0.4 * lijst_y.get(5) + 0.2 * lijst_y.get(6) + 0.1 * lijst_y.get(7)) + (0.1 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.4 * lijst_z.get(5) + 0.2 * lijst_z.get(6) + 0.1 * lijst_z.get(7)) * (0.1 * lijst_z.get(3) + 0.2 * lijst_z.get(4) + 0.4 * lijst_z.get(5) + 0.2 * lijst_z.get(6) + 0.1 * lijst_z.get(7)))) + 0.2 * (java.lang.Math.sqrt((0.1 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.4 * lijst_x.get(6) + 0.2 * lijst_x.get(7) + 0.1 * lijst_x.get(8)) * (0.1 * lijst_x.get(4) + 0.2 * lijst_x.get(5) + 0.4 * lijst_x.get(6) + 0.2 * lijst_x.get(7) + 0.1 * lijst_x.get(8)) + (0.1 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.4 * lijst_y.get(6) + 0.2 * lijst_y.get(7) + 0.1 * lijst_y.get(8)) * (0.1 * lijst_y.get(4) + 0.2 * lijst_y.get(5) + 0.4 * lijst_y.get(6) + 0.2 * lijst_y.get(7) + 0.1 * lijst_y.get(8)) + (0.1 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.4 * lijst_z.get(6) + 0.2 * lijst_z.get(7) + 0.1 * lijst_z.get(8)) * (0.1 * lijst_z.get(4) + 0.2 * lijst_z.get(5) + 0.4 * lijst_z.get(6) + 0.2 * lijst_z.get(7) + 0.1 * lijst_z.get(8)))) - 9.81;
        // oorspronkelijke formule zonder while lus

        //System.out.println("vorige: "+vorige);
        //System.out.println("a:"+a);
        if (vorige_1 == 0) {

            if ( a > drempel) {
                mSteps_1 += 1;
                vorige_1 = 1;
            }
        }
        else {
            if ( a < drempel) {
                vorige_1 = 0;
            }
        }
        //System.out.println("============================ ");

        // al de sout's zijn gebruikt als controle zo kon ik mijn fouten uit de code halen

        index = (index + 1) % 200;
        // natuurlijk schuift de plaats waar we de nieuwe waarden zetten één op iedere keer dat we nieuwe meetresultaten binnenkrijgen
    }






    public int getSteps() {
        return mSteps_1;

    }

    public int getIndex() {
        return index;
    }
}
