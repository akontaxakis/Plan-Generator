package Entities.Comparators;

import Entities.Artifact;
import Entities.Proposition_plan;

import java.util.ArrayList;
import java.util.Comparator;

public class Proposition_plan_comparator_MAX implements Comparator<Proposition_plan> {

    @Override
public int compare(Proposition_plan o1, Proposition_plan o2) {
        return o1.getCost() + heuristic_cost(o1.get_unexpanded_proposition()) - o2.getCost() + heuristic_cost(o2.get_unexpanded_proposition());
        }

    private int heuristic_cost(ArrayList<Artifact> unexpanded_proposition) {
        int max_depth = 0;
        int default_cost = 20;
        for(Artifact a: unexpanded_proposition){
            if(a.getTmp_depth()>=max_depth){
                max_depth = a.getTmp_depth();
            }
        }
        return max_depth*default_cost;
    }
 }


