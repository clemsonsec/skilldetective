package QandA;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jeffreyyoung
 */
public class DataCheck {

    ArrayList<ArrayList<Double>> data;

    DataCheck() {

        System.out.println("Data Check created....");
    }

    //gains, entropy and associated methods from this point---------------------
    public double getGain(ArrayList<Double> decision, ArrayList<Double> list) {
        Set<Double> s = new HashSet<>(list);
        ArrayList<Double> att = new ArrayList<>(s);

        double gain = 0;
        for (int i = 0; i < att.size(); i++) {
            double freq = freqOf(att.get(i), list);
            double freqVar = freq / list.size();
            double a = 0;

            ArrayList<Double> compare = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).equals(att.get(i))) {
                    compare.add(decision.get(j));
                }
            }
            Set<Double> s2 = new HashSet<>(compare);
            ArrayList<Double> att2 = new ArrayList<>(s2);
            for (int k = 0; k < att2.size(); k++) {
                double f = freqOf(att2.get(k), compare) / freq;
                a -= (f * log2(f));
            }

            gain += (freqVar * a);
        }

        return gain;
    }

    //returns entropy
    public double getEntropy(ArrayList<Double> list) {
        Set<Double> s = new HashSet<>(list);
        ArrayList<Double> att = new ArrayList<>(s);
        double freq = freqOf(att.get(0), list) / list.size();
        double entropy = -freq * log2(freq);

        for (int i = 1; i < att.size(); i++) {
            freq = (freqOf(att.get(i), list)) / list.size();
            entropy -= (freq * log2(freq));
        }

        return entropy;
    }

    //returns frequency of a single value
    public double freqOf(double n, ArrayList<Double> a) {
        double count = 0;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).equals(n)) {
                count++;
            }
        }
        return count;
    }

    //reuturns log base 2
    public double log2(double n) {
        return (Math.log(n) / Math.log(2));
    }
    //end gains-----------------------------------------------------------------

    //coorelation begin---------------------------------------------------------
    double correlation(ArrayList<Double> a, ArrayList<Double> b) {
        if (a.size() != b.size()) {
            System.out.println("Error in coorelation. Size of a != Size of b");
        }
        double sum = 0, meanA = mean(a), meanB = mean(b);
        for (int i = 0; i < a.size(); i++) {
            sum += ((a.get(i) - meanA) * (b.get(i) - meanB));
        }
        return sum / ((a.size() - 1) * std(a) * std(b));
    }

    //returns the standard deviation
    double std(ArrayList<Double> a) {
        double sum = 0, mean = mean(a);
        for (int i = 0; i < a.size(); i++) {
            sum += ((a.get(i) - mean) * (a.get(i) - mean));
        }
        return Math.sqrt(sum / (a.size() - 1));
    }

    //returns the mean
    double mean(ArrayList<Double> a) {
        double sum = 0;
        for (int i = 0; i < a.size(); i++) {
            sum += a.get(i);
        }
        return sum / a.size();
    }
    //coorelation end-----------------------------------------------------------

}
