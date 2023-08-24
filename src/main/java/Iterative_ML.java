import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Iterative_ML {


    public static void main(String[] args) {
        lib utils = new lib();
        String uid = "exp3";
        int iterations =6;
        double shared_cummulative_cost =0;
        double limited_cummulative_cost = 0;
        double equivalent_cummulative_cost = 0;
        String project_path= "C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\";
        for(int iteration=0;iteration<iterations;iteration++) {
            ////////////////////////////READ THE GRAPHS/////////////////////////////////
            HyperGraph limited = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid, iteration, "limited");
            HyperGraph shared = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid, iteration, "shared");
            HyperGraph equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid, iteration, "equivalent");
            ////////////////////////////REMOVE SUPER NODES/////////////////////////////////
            HyperGraph true_limited = utils.TurnToTrueHG(limited);
            HyperGraph true_shared = utils.TurnToTrueHG(shared);
            HyperGraph true_equivalent = utils.TurnToTrueHG(equivalent);

            ////////////////////////////READ REQUESTS///////////////////////////////////
            ArrayList<String> mySet= utils.read_requests(project_path, iteration,uid);
            String operation =  mySet.remove(mySet.size()-1);
            double extra_cost = Double.parseDouble( mySet.remove(mySet.size()-1));

            if(mySet.size()==0){
                mySet.add(operation);
            }
            ArrayList<String> eqSet = utils.euqivalentRequests(mySet);
            //String operation = "X_SiStPCLi_predict";

            ArrayList<Artifact> limited_rq = limited.getRequests(new HashSet<>(mySet));
            ArrayList<Artifact> shared_rq = shared.getRequests(operation);
            ArrayList<Artifact> eq_rq = equivalent.getRequests(new HashSet<>(eqSet));

            //loaded_artifacts = limited.collab_forward_pass();
            //HyperGraph collab_plan_2 = limited.collab_backward_pass(loaded_artifacts, limited_rq);

            //System.out.println("collab Cost: " + collab_plan_2.EdgeCost());

            Proposition_plan t_shared_p = true_shared.expand_closest_proposition(shared_rq);
            Proposition_plan t_limited_p = true_limited.expand_closest_proposition(limited_rq);

            Proposition_plan t_eq_p = true_equivalent.expand_closest_proposition(eq_rq);
            double limited_cost =  t_limited_p.getCost() + (extra_cost*1000);
            double eq_cost =  t_eq_p.getCost() + (extra_cost*1000);
            limited_cummulative_cost = limited_cummulative_cost +limited_cost;
            shared_cummulative_cost = shared_cummulative_cost +t_shared_p.getCost();
            equivalent_cummulative_cost = equivalent_cummulative_cost +eq_cost;
            //System.out.println("t_shared" + t_shared_p.getCost());
            //System.out.println("t_limited_p -> " + limited_cost);
            System.out.println(iteration+","+t_shared_p.getCost()+","+limited_cost+","+eq_cost);
            //System.out.println(iteration+","+equivalent_cummulative_cost+","+limited_cummulative_cost+","+shared_cummulative_cost);

        }


    }

}
