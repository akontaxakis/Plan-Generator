package Examples.Heuristic;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class Testing_Heuristics {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        int width = 20;
        int depth = 200;
        int number_of_requests = 5;
        int materialiazed = 40;
        int artifact_in_degree = 2;
        int task_in_degree = 2;
        int task_out_degree = 1;
        int default_cost = 20;

        for (int i = 0; i < 100; i++) {
            AG = new HyperGraph();
            AG.generateRandomLimitedHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
            ArrayList<Artifact> requests = AG.getRequests();

        /*
                                #### BEST-CLOSEST ALGORITHM ####
        */
            startTime = System.nanoTime();
            Proposition_plan closest_plan = AG.expand_closest_proposition(requests);
            //Proposition_plan closest_plan =sche_plan;
            endTime = System.nanoTime();
            long closest_time = ((endTime - startTime) / 1000000);

        /*
                                #### MIN-Heuristic ALGORITHM ####
        */
            startTime = System.nanoTime();
            Proposition_plan min_plan = AG.expand_closest_heuristic_search_MIN(requests);
            endTime = System.nanoTime();
            long min_time = ((endTime - startTime) / 1000000);
        /*
                                #### MAX-Heuristic ALGORITHM ####
        */
            startTime = System.nanoTime();
            Proposition_plan max_plan = AG.expand_closest_heuristic_search_MAX(requests);
            endTime = System.nanoTime();
            long max_time = ((endTime - startTime) / 1000000);
        /*
                                #### Scheduling-Heuristic ALGORITHM ####
        */
            startTime = System.nanoTime();
            AG.compute_the_latency_of_artifacts();
            Proposition_plan sche_plan = AG.expand_closest_heuristic_search_Latency_MIN(requests);
            endTime = System.nanoTime();
            long sche_time = ((endTime - startTime) / 1000000);
         /*
                                #### Scheduling-Heuristic ALGORITHM ####
        */
            startTime = System.nanoTime();
            AG.compute_the_latency_of_artifacts();
            Proposition_plan sche_plan_max = AG.expand_closest_heuristic_search_Latency_MAX(requests);
            endTime = System.nanoTime();
            long sche_time_max = ((endTime - startTime) / 1000000);




            System.out.println(min_plan.getCost() + "," + max_plan.getCost() + "," + sche_plan.getCost() + "," + closest_plan.getCost() +","+sche_plan_max.getCost());
            System.out.println("Time "+min_time + "," + max_time + "," + sche_time + "," + closest_time +"," + sche_time_max);
        }
}
}
