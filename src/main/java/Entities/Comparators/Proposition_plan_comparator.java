package Entities.Comparators;

import Entities.Artifact;
import Entities.Proposition_plan;

import java.util.ArrayList;
import java.util.Comparator;

public class Proposition_plan_comparator implements Comparator<Proposition_plan> {

    @Override
public int compare(Proposition_plan o1, Proposition_plan o2) {
        return o1.getCost() + o1.getHeuristic_cost() - o2.getCost() + o2.getHeuristic_cost();
        }

 }


