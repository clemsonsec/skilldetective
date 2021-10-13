
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jeffreyyoung
 */
public class NN {

    ArrayList<ArrayList<Double>> weights, V;
    boolean delta = true; //checks for output delta values to be negligible
    double deltaAvg = 0.0, max, min, min1, max1, scaler = 10.0;

    NN() {

        System.out.println("NN Object created....");

    }

    void run(ArrayList<ArrayList<Double>> data) {

        //train
        ArrayList<ArrayList<Double>> Xn = new ArrayList<>();
        //cl
        ArrayList<ArrayList<Double>> Tn = new ArrayList<>();

        for (int i = 0; i < data.size() - 1; i++) {
            Xn.add(data.get(i));
        }
        Tn.add(data.get(data.size() - 1));

        System.out.println("X size: " + Xn.size() + " with " + Xn.get(0).size() + " values"); //X
        System.out.println("T size: " + Tn.size() + " with " + Tn.get(0).size() + " values"); //D

        //normalize data in Xn
        for (int i = 0; i < Xn.size(); i++) {
            max1 = Collections.max(Xn.get(i));
            min1 = Collections.min(Xn.get(i));
            if (max1 > 1) { //normalizes non-binary data > 1
                for (int j = 0; j < Xn.get(i).size(); j++) {
                    double temp = Xn.get(i).get(j);
                    Xn.get(i).remove(j);
                    Xn.get(i).add(j, norm(temp, min1, max1) / scaler);
                }
            }
        }
        //data normalized for D

        max = Collections.max(Tn.get(0));
        min = Collections.min(Tn.get(0));
        for (int j = 0; j < Tn.get(0).size(); j++) {
            double temp = Tn.get(0).get(j);
            Tn.get(0).remove(j);
            Tn.get(0).add(j, norm(temp, min, max) / scaler);
        }

        System.out.println("X size: " + Xn.size() + " with " + Xn.get(0).size() + " values"); //X
        System.out.println("T size: " + Tn.size() + " with " + Tn.get(0).size() + " values"); //D

        //set learning rate
        double lR = .85;
        System.out.println("Learning rate: " + lr);
        //set # of nodes in each layer
        int nodes = 5;
        System.out.println("# of nodes in hidden layer: " + nodes);
        //set the tolerance
        double toler = 0.05;
        System.out.println("Tolerance: " + toler);
        // if print set to 'y', all values are printed while system trains
        System.out.println("Print Values? (y or n) ");
        String print = "n";

        System.out.println("X size after test: " + Xn.size());
        System.out.println("X size after test: " + Xn.size() + " with " + Xn.get(0).size() + " values"); //X
        System.out.println("T size after test: " + Tn.size() + " with " + Tn.get(0).size() + " values"); //D

        //initialize W and V----------------------------------------------------
        weights = new ArrayList<>();
        initW(weights, nodes, Xn.size()); //initialize weights in W
        V = new ArrayList<>();
        initV(V, nodes, Tn.size()); //initialize weights in V
        System.out.println("Intitial Weight Values: ");
        printD(weights);
        System.out.println("Intitial V Values: ");
        printD(V);
        //end init weights------------------------------------------------------

        int epoch = 0;
        while (delta) {

            int count = 0;
            for (int i = 0; i < Xn.get(0).size(); i++) {

                //feeds input
                ArrayList<Double> X = new ArrayList<>();
                for (int j = 0; j < Xn.size(); j++) {
                    X.add(Xn.get(j).get(i));
                }
                //feeds expected output value
                ArrayList<Double> T = new ArrayList<>();
                for (int j = 0; j < Tn.size(); j++) {
                    T.add(Tn.get(j).get(i));
                }
                count += forward(X, T, lR, toler, print);

            }
            if (count == Xn.get(0).size()) {

                delta = false;
            }
            //adjust # of desired epochs. Generally 1 is enough for a large training set
            if (epoch == 1) {
                delta = false;
            }
            System.out.println("goal: " + Xn.get(0).size());
            System.out.println("Number of OP within toler: " + count);
            System.out.println("Average delta: " + (deltaAvg / Xn.get(0).size()));
            deltaAvg = 0.0;
            System.out.println("Epoch#: " + ++epoch);
        }

        System.out.println("System is trained...");
    }

    //used for test set
    public Double predict(ArrayList<Double> X) {

        //max1 = Collections.max(X);
        //min1 = Collections.min(X);
        for (int i = 0; i < X.size(); i++) {
            if (max1 > 1) { //normalizes non-binary data > 1
                double temp = X.get(i);
                X.remove(i);
                X.add(i, norm(temp, min1, max1) / scaler);
            }
        }

        //calculate values for R
        ArrayList<Double> R = new ArrayList<>();
        for (int i = 0; i < weights.size(); i++) {
            double sum = 0;
            for (int j = 0; j < weights.get(i).size(); j++) {
                sum += (weights.get(i).get(j) * X.get(j));
            }
            R.add(sigmoid(sum));
        }
        //calculate values for O
        ArrayList<Double> O = new ArrayList<>();
        for (int i = 0; i < V.size(); i++) {
            double sum = 0;
            for (int j = 0; j < V.get(i).size(); j++) {
                sum += (V.get(i).get(j) * R.get(j));
            }
            O.add(sigmoid(sum));
        }

        for (int i = 0; i < O.size(); i++) {
            System.out.println("Actual Output Norm: " + O.get(i) * scaler);
            System.out.println("Actual Output unNorm: " + unNorm(O.get(i), min, max) * scaler);

        }

        if (O.size() == 1) {
            return unNorm(O.get(0), min, max) * scaler;
        } else {
            return -1.0;
        }
    }

    //X = input, T = Decision, lRate = learning rate
    public int forward(ArrayList<Double> X, ArrayList<Double> T, double lRate, double toler, String print) {
        //calculate values for R
        ArrayList<Double> R = new ArrayList<>();
        for (int i = 0; i < weights.size(); i++) {
            double sum = 0;
            for (int j = 0; j < weights.get(i).size(); j++) {
                sum += (weights.get(i).get(j) * X.get(j));
            }
            R.add(sigmoid(sum));
        }
        //calculate values for O
        ArrayList<Double> O = new ArrayList<>();
        for (int i = 0; i < V.size(); i++) {
            double sum = 0;
            for (int j = 0; j < V.get(i).size(); j++) {
                sum += (V.get(i).get(j) * R.get(j));
            }
            O.add(sigmoid(sum));
        }

        //checks the delta values for ecpected and predicted out----------------
        int count = 0;
        for (int i = 0; i < O.size(); i++) {
            Double d = Math.abs(O.get(i) - T.get(i));
            deltaAvg += d;
            if (d <= toler) {
                count++;
            }

            if ("y".equals(print)) {
                System.out.println("Expected Output: " + T.get(i));
                System.out.println("Actual Output: " + O.get(i));
                System.out.println("Delta: " + d);
            }
        }

        //end check-------------------------------------------------------------
        ArrayList<Double> Dk = new ArrayList<>();
        if (O.size() == T.size()) {
            for (int i = 0; i < O.size(); i++) {
                Dk.add(O.get(i) * ((T.get(i) - O.get(i)) * (1 - O.get(i))));
            }
        } else {
            System.out.println("O != T");
        }
        //Back Prop begins------------------------------------------------------
        //update V
        newV(R, Dk, lRate, print);
        //update weights
        newW(R, Dk, X, lRate, print);
        //back prop ends with new V and W---------------------------------------

        if (count == O.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    public void newW(ArrayList<Double> R, ArrayList<Double> dK, ArrayList<Double> X, double l, String print) {

        //R$(I - R)
        ArrayList<Double> Rnew = new ArrayList<>();
        for (int i = 0; i < R.size(); i++) {
            Rnew.add(R.get(i) * (1 - R.get(i)));
        }

        //DkV^t
        ArrayList<Double> dKnew = new ArrayList<>();
        for (int i = 0; i < V.get(0).size(); i++) {
            double sum = 0;
            for (int j = 0; j < V.size(); j++) {
                sum += (dK.get(j) * V.get(j).get(i));
            }
            dKnew.add(sum);
        }

        //Dj = Dk(V^t)$R$(I - R)
        ArrayList<Double> Dj = new ArrayList<>();
        for (int i = 0; i < Rnew.size(); i++) {
            Dj.add(Rnew.get(i) * dKnew.get(i));
        }

        for (int i = 0; i < X.size(); i++) {
            for (int j = 0; j < Dj.size(); j++) {
                double temp = weights.get(j).get(i) + (l * X.get(i) * Dj.get(j)); //Dj.get(i) was switched. Check
                weights.get(j).remove(i);
                weights.get(j).add(i, temp);

            }
        }

        if (print.equals("y")) {
            System.out.println("New Weights: ");
            printD(weights);
        }
    }

    public void newV(ArrayList<Double> R, ArrayList<Double> dK, double l, String print) {

        for (int i = 0; i < V.size(); i++) {
            for (int j = 0; j < V.get(i).size(); j++) {
                double temp = V.get(i).get(j) + (l * R.get(j) * dK.get(i));
                V.get(i).remove(j);
                V.get(i).add(j, temp);
            }
        }
        if (print.equals("y")) {
            System.out.println("new V: ");
            printD(V);
        }

    }

    public void initW(ArrayList<ArrayList<Double>> W, int nodes, int inputs) {
        for (int i = 0; i < nodes; i++) {
            W.add(new ArrayList<>());
            for (int j = 0; j < inputs; j++) {
                double r = Math.random();
                if (r == 0.0) {
                    r += 0.001;
                }
                W.get(W.size() - 1).add(r);
            }
        }
    }

    public void initV(ArrayList<ArrayList<Double>> V, int nodes, int inputs) {
        for (int i = 0; i < inputs; i++) {
            V.add(new ArrayList<>());
            for (int j = 0; j < nodes; j++) {
                double r = Math.random();
                if (r == 0.0) {
                    r += 0.001;
                }
                V.get(V.size() - 1).add(r);
            }
        }

    }

    //builds test and training sets
    public ArrayList<ArrayList<Double>> buildSets(ArrayList<ArrayList<Double>> d, double percent) {

        int shift = (int) (100 * percent);
        ArrayList<ArrayList<Double>> temp = new ArrayList<>();
        for (int i = 0; i < d.size(); i++) {
            temp.add(new ArrayList<>());
        }
        for (int i = 0; i < percent * d.get(0).size(); i++) {
            for (int j = 0; j < d.size(); j++) {
                temp.get(j).add(d.get(j).get(i + shift));
                d.get(j).remove(i + shift);
            }
        }

        return temp;

    }

    public double sigmoid(double net) {
        return 1.0 / (1.0 + Math.exp(-net));
    }

    public double norm(double x, double min, double max) {
        return (x - min) / (max - min);
    }

    public double unNorm(double x, double min, double max) {
        return x * (max - min) + min;
    }

    public void printD(ArrayList<ArrayList<Double>> d) {

        for (int i = 0; i < d.get(0).size(); i++) {
            for (int j = 0; j < d.size(); j++) {
                System.out.print(d.get(j).get(i) + " ");
            }
            System.out.println();
        }
    }

    public void printS(ArrayList<ArrayList<String>> d) {

        for (int i = 0; i < d.get(0).size(); i++) {
            for (int j = 0; j < d.size(); j++) {
                System.out.print(d.get(j).get(i) + " ");
            }
            System.out.println();
        }
    }

}
