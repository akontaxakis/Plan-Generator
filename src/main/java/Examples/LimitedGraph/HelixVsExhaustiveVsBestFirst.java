package Examples.LimitedGraph;

import DirectedGraph.DirectedGraph;
import DirectedGraph.MaximumFlow;
import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class HelixVsExhaustiveVsBestFirst {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        int width = 10000;
        int depth = 100;
        int number_of_requests = 1;
        int materialiazed = 60;
        int artifact_in_degree = 0;
        int task_in_degree = 1;
        int task_out_degree = 0;
        int default_cost = 20;

        System.out.println(number_of_requests + "," + width + "," + depth + "," + materialiazed + "," + artifact_in_degree + "," + task_in_degree + "," + task_out_degree + "," + default_cost);


        for (int i = 0; i < 100; i++) {
            AG = new HyperGraph();
            AG.generateRandomLimitedHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
            ArrayList<Artifact> requests = AG.getRequests();

        /*
                                #### Collaborative ALGORITHM ####

            startTime = System.nanoTime();

            ArrayList<Artifact> loaded_artifacts = AG.collab_forward_pass();
            HyperGraph collab_plan = AG.collab_backward_pass(loaded_artifacts, requests);
            endTime = System.nanoTime();
            long collab_time = ((endTime - startTime) / 1000000);
           */
        /*
                                #### Exhaustive ALGORITHM ####
        */
            startTime = System.nanoTime();
            //AG.compute_the_latency_of_artifacts();
            Proposition_plan exhaustive_search_Plan = AG.iterative_exhaustive(requests);
            endTime = System.nanoTime();
            long ex_time = ((endTime - startTime) / 1000000);
            long number_of_plans = AG.numberOfPlans;

                /*
                             #### Best SEARCH ####
                */
            startTime = System.nanoTime();
            Proposition_plan closest_proposition = AG.expand_closest_proposition(requests);
            endTime = System.nanoTime();
            long closest_time = ((endTime - startTime) / 1000000);

                /*
                             #### Best SEARCH ####
                */
            startTime = System.nanoTime();
            AG.compute_the_latency_of_artifacts();
            Proposition_plan heuristic_proposition = AG.expand_closest_heuristic_search_Latency_MIN(requests);
            endTime = System.nanoTime();
            long heuristic_time = ((endTime - startTime) / 1000000);


            System.out.println(number_of_plans + "," + AG.getArtifactsSize() + "," + AG.getEdgeSize() + "," +ex_time + "," + closest_time + "," + heuristic_time);
        }

}
}
