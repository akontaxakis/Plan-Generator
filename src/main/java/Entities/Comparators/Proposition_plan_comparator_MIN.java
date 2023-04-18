package Entities.Comparators;

import Entities.Artifact;
import Entities.Proposition_plan;

import java.util.ArrayList;
import java.util.Comparator;

public class Proposition_plan_comparator_MIN implements Comparator<Proposition_plan> {

    @Override
public int compare(Proposition_plan o1, Proposition_plan o2) {
        return o1.getCost() + heuristic_cost(o1.get_unexpanded_proposition()) - o2.getCost() + heuristic_cost(o2.get_unexpanded_proposition());
        }

    private int heuristic_cost(ArrayList<Artifact> unexpanded_proposition) {
        int min = 10000;
        int default_cost = 20;
        for(Artifact a: unexpanded_proposition){
            if(a.get_heuristic_cost(default_cost)<=min){
                min = a.get_heuristic_cost(default_cost);
            }
        }
        return min ;
    }
 }


