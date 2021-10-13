/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 * @author jeffreyyoung
 */
public class Map {

    //ques
    ArrayList<String> nodes = new ArrayList<>();
    //answers
    ArrayList<String> branches = new ArrayList<>();

    public Map() {

        System.out.println("Map created....");

    }

    public void addToNodes(String node) {
        nodes.add(node);
    }

    public void addToBranches(String branch) {
        branches.add(branch);
    }

    public ArrayList<String> getNodes() {
        return nodes;
    }

    public ArrayList<String> getBranches() {
        return branches;
    }

    /**
     * copies map1 into map2
     *
     * @param map1
     * @param map2
     */
    public void copy(Map map1, Map map2) {

        if (map2.getBranches().isEmpty()) {
            for (String s : map1.getBranches()) {
                map2.getBranches().add(s);
            }
            for (String s : map1.getNodes()) {
                map2.getNodes().add(s);
            }
        }
    }

    //true if both maps are identical
    public boolean compare(Map m1, Map m2) {

        if ((m1.getBranches().size() == m2.getBranches().size())
                && (m1.getNodes().size() == m2.getNodes().size())) {
            for (int i = 0; i < m1.getBranches().size(); i++) {
                if (!m1.getBranches().get(i).equals(m2.getBranches().get(i))) {
                    return false;
                }
                if (!m1.getNodes().get(i).equals(m2.getNodes().get(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

}
