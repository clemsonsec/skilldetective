
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sim {

    private static ILexicalDatabase db = new NictWordNet();

    //available options of metrics
    static RelatednessCalculator[] rcs = {
        new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
        new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)};

    RelatednessCalculator rc = new WuPalmer(db);

    ArrayList<ArrayList<String>> data = new ArrayList<>();
    ArrayList<String> BOW = new ArrayList<>();
    HashMap<Double, String> classes = new HashMap<>();

    ArrayList<ArrayList<String>> sW = new ArrayList<>(); //stores inputs

    Sim(ArrayList<ArrayList<String>> data, ArrayList<ArrayList<String>> sW) {

        this.data = data;
        this.sW = sW;
        BOW();
        System.out.println("Object created.....");
        System.out.println("BOW size: " + BOW.size() + " " + BOW.toString());

    }

    public double compute(String word1, String word2) {

        WS4JConfiguration.getInstance().setMFS(true);
        //double s = new Lin(db).calcRelatednessOfWords(word1, word2);
        double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);

        if (s < 1.0) {
            return s;
        } else {
            return 1.0;
        }

    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     *
     * @param s1
     * @param s2
     */
    public double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
            /* both strings are zero length */ }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
// Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java

    public int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    public ArrayList<ArrayList<Double>> getVecData(ArrayList<ArrayList<String>> data, int bit) {

        ArrayList<ArrayList<Double>> d = new ArrayList<>();
        System.out.println("Vectorizing data....");

        for (int i = 0; i < BOW.size(); i++) {
            d.add(new ArrayList<>());
        }

        //vectorize the questions
        for (int i = 0; i < data.get(0).size(); i++) {
            ArrayList<String> t = new ArrayList<>();
            split(data.get(0).get(i), t);
            for (int j = 0; j < BOW.size(); j++) {
                double sum = 0.0;
                for (int k = 0; k < t.size(); k++) {
                    if(t.get(k).toLowerCase().equals(BOW.get(j).toLowerCase())){
                        sum++;
                    }
                   
                    //sum += compute(t.get(k).toLowerCase(), BOW.get(j).toLowerCase());
                }

                d.get(j).add(sum / t.size());

            }
            System.out.println(i);
        }

        if (bit == 1) {
            Set hs = new HashSet(data.get(data.size() - 1));
            ArrayList<String> a = new ArrayList<>(hs);

            d.add(new ArrayList<>());
            for (int j = 0; j < data.get(data.size() - 1).size(); j++) {
                d.get(d.size() - 1).add(-1.0);
            }
            double cl = 1.0;
            for (int i = 0; i < a.size(); i++) {
                System.out.println(a.get(i) + " = " + cl);
                classes.put(cl, a.get(i));
                for (int j = 0; j < data.get(data.size() - 1).size(); j++) {
                    if (a.get(i).equals(data.get(data.size() - 1).get(j))) {
                        d.get(d.size() - 1).remove(j);
                        d.get(d.size() - 1).add(j, cl);
                    }
                }
                cl++;
            }
        }

        return d;
    }

    public final void BOW() {

        ArrayList<String> s = new ArrayList<>(data.get(0));
        Set hs = new HashSet<>();
        for (int i = 0; i < s.size(); i++) {
            ArrayList<String> t = new ArrayList<>();
            split(s.get(i).toLowerCase(), t);
            hs.add(t.get(t.size() / 2).toLowerCase());
            //hs.add(t.get(t.size() - 1).toLowerCase());
            /*for (int j = 0; j < t.size(); j++) {
                hs.add(t.get(j).toLowerCase());
            }*/
        }

        BOW = new ArrayList<>(hs);

    }

    public static double norm(double x, double min, double max) {
        return (x - min) / (max - min);
    }

    public static void split(String s, ArrayList<String> a) {

        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                if (s.charAt(i) != ',' && s.charAt(i) != '.' && s.charAt(i) != '?') {
                    temp += s.charAt(i);
                }
            } else {
                a.add(temp.replaceAll("\\p{Punct}", "").toLowerCase());
                temp = "";
            }
        }

        if (!temp.isBlank()) {
            //adds the last word in the statement
            a.add(temp.replaceAll("\\p{Punct}", "").toLowerCase());
        }

    }

}
