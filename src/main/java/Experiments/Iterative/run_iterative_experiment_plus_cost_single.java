package Experiments.Iterative;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.HashSet;

public class run_iterative_experiment_plus_cost_single {


    public static void main(String[] args) {
        lib utils = new lib();
        String uid = "ex100";
        uid = "b8i150";
        uid = "LOtoSingle";
        //uid = "b100s1000000";
        int iterations =12;
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
                ArrayList<String> mySet = utils.read_requests(project_path, iteration, uid+"_"+budget);
                ArrayList<String> eqSet_2 = utils.read_requests(project_path, iteration, uid+"_eq_"+budget);

                String operation = mySet.remove(mySet.size() - 1);
                double extra_cost = Double.parseDouble(mySet.remove(mySet.size() - 1));

                if (mySet.size() == 0) {
                    mySet.add(operation);
                }

                eqSet_2.remove(eqSet_2.size()-1);
                double extra_cost_2 = Double.parseDouble(eqSet_2.remove(eqSet_2.size()-1));
                if (eqSet_2.size() == 0) {
                    eqSet_2.add(operation);
                }

                ArrayList<String> eqSet = utils.euqivalentRequests(eqSet_2);
                //String operation = "X_SiStPCLi_predict";

                ArrayList<Artifact> limited_rq = true_limited.getRequests(new HashSet<>(mySet));
                ArrayList<Artifact> shared_rq = true_shared.getRequests(operation);
                ArrayList<Artifact> eq_rq = true_equivalent.getRequests(new HashSet<>(eqSet));

                //loaded_artifacts = limited.collab_forward_pass();
                //HyperGraph collab_plan_2 = limited.collab_backward_pass(loaded_artifacts, limited_rq);

                //System.out.println("collab Cost: " + collab_plan_2.EdgeCost());

                Proposition_plan t_shared_p = true_shared.iterative_exhaustive(shared_rq);

                ArrayList<Artifact> loaded_artifacts = true_limited.collab_forward_pass();
                HyperGraph t_limited_p = true_limited.collab_backward_pass(loaded_artifacts, limited_rq);



                Proposition_plan t_eq_p = true_equivalent.expand_closest_proposition(eq_rq);

                double limited_cost = t_limited_p.EdgeCost() + (extra_cost * 10000);
                double eq_cost = t_eq_p.getCost() + (extra_cost_2 * 10000);
                limited_cummulative_cost = limited_cummulative_cost + limited_cost;
                shared_cummulative_cost = shared_cummulative_cost + t_shared_p.getCost();
                equivalent_cummulative_cost = equivalent_cummulative_cost + eq_cost;
                if(limited_cost < eq_cost){
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
