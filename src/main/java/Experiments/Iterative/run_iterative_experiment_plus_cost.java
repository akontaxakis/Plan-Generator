package Experiments.Iterative;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.HashSet;

public class run_iterative_experiment_plus_cost {


    public static void main(String[] args) {
        lib utils = new lib();
        String uid = "ex100";
        uid = "b8i150";
        uid = "LOtoSingle2";
        //uid = "b100s1000000";
        int iterations =63;
        int b = 10;

        String project_path= "C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\";
        for(int budget=0; budget<b;budget++){
            double shared_cummulative_cost =0;
            double limited_cummulative_cost = 0;
            double equivalent_cummulative_cost = 0;
            for (int iteration = 8; iteration < iterations; iteration++) {
                ////////////////////////////READ THE GRAPHS////////////////////////////////////
                HyperGraph shared = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid,iteration , "shared");
                HyperGraph limited = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid+"_"+budget, iteration, "limited");
                HyperGraph equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid+"_"+budget, iteration, "equivalent");
                ////////////////////////////REMOVE SUPER NODES/////////////////////////////////
                HyperGraph true_limited = utils.TurnToTrueHG(limited);
                HyperGraph true_shared = utils.TurnToTrueHG(shared);
                HyperGraph true_equivalent = utils.TurnToTrueHG(equivalent);

                ////////////////////////////READ REQUESTS///////////////////////////////////
                ArrayList<String> ltSet = utils.read_requests(project_path, iteration, uid+"_"+budget);
                ArrayList<String> eqSet_ = utils.read_requests(project_path, iteration, uid+"_eq_"+budget);

                String operation = ltSet.remove(ltSet.size() - 1);
                double lt_extra_cost = Double.parseDouble(ltSet.remove(ltSet.size() - 1));

                if (ltSet.size() == 0) {
                    ltSet.add(operation);
                }

                eqSet_.remove(eqSet_.size()-1);
                double eq_extra_cost = Double.parseDouble(eqSet_.remove(eqSet_.size()-1));
                if (eqSet_.size() == 0) {
                    eqSet_.add(operation);
                }

                ArrayList<String> eqSet = utils.euqivalentRequests(eqSet_);
                //String operation = "X_SiStPCLi_predict";

                ArrayList<Artifact> lt_rq = true_limited.getRequests(new HashSet<>(ltSet));
                ArrayList<Artifact> sh_rq = true_shared.getRequests(operation);
                ArrayList<Artifact> eq_rq = true_equivalent.getRequests(new HashSet<>(eqSet));

                Proposition_plan sh_plan = true_shared.iterative_exhaustive(eq_rq);
                Proposition_plan lt_plan = true_limited.expand_closest_proposition(sh_rq);
                Proposition_plan eq_plan = true_equivalent.expand_closest_proposition(eq_rq);

                double sh_cost = sh_plan.getCost();
                double lt_cost = lt_plan.getCost() + (lt_extra_cost * 10000);
                double eq_cost = eq_plan.getCost() + (eq_extra_cost * 10000);

                shared_cummulative_cost = shared_cummulative_cost + sh_cost;
                limited_cummulative_cost = limited_cummulative_cost + lt_cost;
                equivalent_cummulative_cost = equivalent_cummulative_cost + eq_cost;


                if(eq_cost > sh_cost +10000){
                    System.out.println("problem in " + iteration);
                }
                //System.out.println("t_shared" + t_shared_p.getCost());
                //System.out.println("t_limited_p -> " + limited_cost);
                //System.out.println(iteration+","+t_shared_p.getCost()+","+limited_cost+","+eq_cost);
                //System.out.println(iteration + "," + equivalent_cummulative_cost + "," + limited_cummulative_cost + "," + shared_cummulative_cost);

            }
            //System.out.println(budget + "," + shared_cummulative_cost + "," + limited_cummulative_cost + "," + equivalent_cummulative_cost);
            double edge_cost_modifier = 1000;
            double dataset_size = 0.8;
            double budget_cost = dataset_size * 0.023 ;
            double computation_cost_per_hour = 0.63;
            double computation_cost_per_second = computation_cost_per_hour/3600;
            double shared_cost = shared_cummulative_cost/edge_cost_modifier*computation_cost_per_second + budget_cost;
            if(budget > 0)
            budget_cost = (0.0000008 * Math.pow(10,budget) * 0.023) + 0.8 * 0.023;
            double limited_cost = limited_cummulative_cost/edge_cost_modifier*computation_cost_per_second + budget_cost;
            double equivalent_cost = equivalent_cummulative_cost/edge_cost_modifier*computation_cost_per_second+ budget_cost;
            System.out.println(budget + "," + shared_cummulative_cost/edge_cost_modifier + "," + limited_cummulative_cost/edge_cost_modifier + "," + equivalent_cummulative_cost/edge_cost_modifier + "," + shared_cost + "," + limited_cost + "," + equivalent_cost);

        }
    }

}
