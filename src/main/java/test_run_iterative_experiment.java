import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.HashSet;

public class test_run_iterative_experiment {


    public static void main(String[] args) {
        lib utils = new lib();
        String uid = "ex100";
        uid = "b8i150";
        uid = "b100s1000000";
        int iterations =2;
        int b = 3;

        String project_path= "C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\";
        for(int budget=0; budget<b;budget++){
            double shared_cummulative_cost =0;
            double limited_cummulative_cost = 0;
            double equivalent_cummulative_cost = 0;
            for (int iteration = 1; iteration < iterations; iteration++) {
                ////////////////////////////READ THE GRAPHS/////////////////////////////////

                HyperGraph shared = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid,iteration , "shared");
                HyperGraph limited = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid+"_"+budget, iteration+1, "limited");
                HyperGraph equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid+"_"+budget, iteration+1, "equivalent");
                ////////////////////////////REMOVE SUPER NODES/////////////////////////////////
                HyperGraph true_limited = utils.TurnToTrueHG(limited);
                HyperGraph true_shared = utils.TurnToTrueHG(shared);
                HyperGraph true_equivalent = utils.TurnToTrueHG(equivalent);

                ////////////////////////////READ REQUESTS///////////////////////////////////
                ArrayList<String> mySet = utils.read_requests(project_path, iteration+1, uid+"_"+budget);
                ArrayList<String> eqSet_2 = utils.read_requests(project_path, iteration+1, uid+"_eq_"+budget);

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

                ArrayList<Artifact> limited_rq = limited.getRequests(new HashSet<>(mySet));
                ArrayList<Artifact> shared_rq = shared.getRequests(new HashSet<>(mySet));
                ArrayList<Artifact> eq_rq = equivalent.getRequests(new HashSet<>(eqSet));

                //loaded_artifacts = limited.collab_forward_pass();
                //HyperGraph collab_plan_2 = limited.collab_backward_pass(loaded_artifacts, limited_rq);

                //System.out.println("collab Cost: " + collab_plan_2.EdgeCost());

                Proposition_plan t_shared_p = true_shared.iterative_exhaustive(shared_rq);
                Proposition_plan t_limited_p = true_limited.iterative_exhaustive(limited_rq);

                Proposition_plan t_eq_p = true_equivalent.iterative_exhaustive(eq_rq);
                double limited_cost = t_limited_p.getCost();
                double eq_cost = t_eq_p.getCost();
                limited_cummulative_cost = limited_cummulative_cost + limited_cost;
                shared_cummulative_cost = shared_cummulative_cost + t_shared_p.getCost();
                equivalent_cummulative_cost = equivalent_cummulative_cost + eq_cost;
                //System.out.println("t_shared" + t_shared_p.getCost());
                //System.out.println("t_limited_p -> " + limited_cost);
                System.out.println(iteration+","+t_shared_p.getCost()+","+limited_cost+","+eq_cost);
                //System.out.println(iteration + "," + equivalent_cummulative_cost + "," + limited_cummulative_cost + "," + shared_cummulative_cost);

            }
            //System.out.println(budget + "," + shared_cummulative_cost + "," + limited_cummulative_cost + "," + equivalent_cummulative_cost);
            double shared_cost = shared_cummulative_cost*0.00009;
            double budget_cost = (0.07 * Math.pow(10,budget) * 0.02);
            double limited_cost = limited_cummulative_cost*0.00009 + budget_cost;
            double equivalent_cost = equivalent_cummulative_cost*0.00009+ budget_cost;
            System.out.println(budget_cost + "," + shared_cummulative_cost + "," + limited_cummulative_cost + "," + equivalent_cummulative_cost + "," + shared_cost + "," + limited_cost + "," + equivalent_cost);

        }
    }

}
