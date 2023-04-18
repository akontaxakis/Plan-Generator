package Examples.LimitedGraph;

import DirectedGraph.DirectedGraph;
import DirectedGraph.MaximumFlow;
import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class HelixVsCollabVsClosestVsFastVsHeuristic {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        int width = 100;
        int depth = 10;
        int number_of_requests = 10;
        int materialiazed = 40;
        int artifact_in_degree = 0;
        int task_in_degree = 1;
        int task_out_degree = 0;
        int default_cost = 20;


        for (int i = 1; i < 101; i++) {
            number_of_requests = i;
            AG = new HyperGraph();
            AG.generateRandomLimitedHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);

            ArrayList<Artifact> requests = AG.getRequests();

        /*
                                #### Collaborative ALGORITHM ####
        */
            startTime = System.nanoTime();

            ArrayList<Artifact> loaded_artifacts = AG.collab_forward_pass();
            HyperGraph collab_plan = AG.collab_backward_pass(loaded_artifacts, requests);
            endTime = System.nanoTime();
            long collab_time = ((endTime - startTime) / 1000000);

        /*
                                #### Naive ALGORITHM ####
        */
            startTime = System.nanoTime();
            AG.compute_the_latency_of_artifacts();
            Proposition_plan ex_search_Plan = AG.iterative_exhaustive(requests);
            endTime = System.nanoTime();
            long ex_time = ((endTime - startTime) / 1000000);


        /*
                                #### HEURISTIC Search ALGORITHM ####
        */
            startTime = System.nanoTime();
            AG.compute_the_latency_of_artifacts();
            Proposition_plan Heuristic_search_Plan = AG.expand_closest_heuristic_search_Latency_MIN(requests);
            endTime = System.nanoTime();
            long h_time = ((endTime - startTime) / 1000000);

        /*
                             #### HELIX SEARCH ####
        */
            startTime = System.nanoTime();
            DirectedGraph g = AG.toHelixGraph(requests.get(requests.size() - 1).getPosition() * 2 + 2);
            MaximumFlow mx = new MaximumFlow(g);
            ArrayList<Integer> flow = mx.getRisidualGraph(0, requests.get(requests.size() - 1).getPosition() * 2 + 2);

          //  HyperGraph HelixPlan = AG.flowToAG(flow);
            endTime = System.nanoTime();
            long helix_time = ((endTime - startTime) / 1000000);
                /*
                             #### Closest SEARCH ####
                */
            startTime = System.nanoTime();
            Proposition_plan closest_proposition = AG.expand_closest_proposition(requests);
            endTime = System.nanoTime();
            long closest_time = ((endTime - startTime) / 1000000);

            System.out.println(AG.numberOfPlans+","+number_of_requests+","+AG.getArtifactsSize() + "," + AG.getEdgeSize() +","+helix_time + "," + ex_time + "," + closest_time + "," + h_time + "," + collab_time);
        }
}
}
