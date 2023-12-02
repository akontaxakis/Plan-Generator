package Experiments.Scalability;

import DirectedGraph.DirectedGraph;
import DirectedGraph.MaximumFlow;
import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class Limited{

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        int width = 5;
        lib utils = new lib();
        int depth2 = 10;
        int depth = 10;
        int number_of_requests = 10;
        int materialiazed = 40;
        int artifact_in_degree = 0;
        int task_in_degree = 5;
        int task_out_degree = 1;
        int default_cost = 20;
        int n =10;

        for (int i = 1; i < 10; i++) {
            long stack_time = 0;
            long queue_time = 0;
            long heuristic_queue_time = 0;

            long collab_time = 0;
            long helix_time = 0;
            long priority_queue_time = 0;

            long collab_cost = 0;
            long helix_cost = 0;
            long priority_cost = 0;
            long size = 0;
            long edges = 0;
            for (int k = 1; k < 10; k++) {
            depth = depth2 * i * 10;
            number_of_requests = k;


            for (int j = 0; j < n; j++) {
                AG = new HyperGraph();
                AG.generateRandomLimitedHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
                size = size + AG.getArtifactsSize();
                edges = edges + AG.getEdgeSize();

                ArrayList<Artifact> requests = AG.getRequests2();

        /*
                                #### Collaborative ALGORITHM ####
        */
                startTime = System.nanoTime();

                ArrayList<Artifact> loaded_artifacts = AG.collab_forward_pass();
                HyperGraph collab_plan = AG.collab_backward_pass(loaded_artifacts, requests);
                collab_cost = collab_cost + collab_plan.EdgeCost();
                endTime = System.nanoTime();
                collab_time = collab_time + ((endTime - startTime) / 1000);

        /*
                             #### HELIX SEARCH ####
        */
                startTime = System.nanoTime();
                DirectedGraph g = AG.toHelixGraph(requests.get(requests.size() - 1).getPosition() * 2 + 2);
                MaximumFlow mx = new MaximumFlow(g);
                ArrayList<Integer> flow = mx.getRisidualGraph(0, requests.get(requests.size() - 1).getPosition() * 2 + 2);
                HyperGraph HelixPlan = AG.flowToAG(flow);

                helix_cost = helix_cost + HelixPlan.EdgeCost();
                endTime = System.nanoTime();
                helix_time = helix_time + ((endTime - startTime) / 1000);
                      /*

                /*
                             #### priortiy_queue_search ####
                */
                startTime = System.nanoTime();
                Proposition_plan priority_que_proposition = AG.priortiy_queue_search_with_MIN_heuristic(requests);
                endTime = System.nanoTime();
                priority_cost =  priority_cost + priority_que_proposition.getCost();
                priority_queue_time = priority_queue_time+ ((endTime - startTime) / 1000);
                //System.out.println(i + "," +size + "," + edges+"," + helix_time + "," +collab_time+"," + priority_queue_time +","+helix_cost+","+collab_cost+","+priority_cost );

            }
            //System.out.println("AVG" +i + "," +size/10 + "," + edges/10 +"," + helix_time/n + "," +collab_time/n+"," + priority_queue_time/n +","+helix_cost/n+","+collab_cost/n+","+priority_cost/n );
            System.out.println(i+ ","+ k + "," + size / 10 + "," + edges / 10 + "," + helix_time / n + "," + collab_time / n + "," + helix_cost / n + "," + collab_cost / n);
        }
            System.out.println(i+ "," +depth + "," +size / 100 + "," + edges / 100 + "," + helix_time / 100 + "," + collab_time / 100 +"," + priority_queue_time/n + "," + helix_cost / 100 + "," + collab_cost / 100+","+priority_cost/n );

        }
    }
}
