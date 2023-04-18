package Examples;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class Example_sheduling {
    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;

        int width = 200;
        int depth = 500;
        int number_of_requests = 5;
        int materialiazed = 20;
        int artifact_in_degree = 2;
        int task_in_degree = 2;
        int task_out_degree = 2;
        int default_cost = 10;


        System.out.println("Test Parameters");
        System.out.println(number_of_requests + "," + width + "," + depth + "," + materialiazed + "," + artifact_in_degree + "," + task_in_degree + "," + task_out_degree + "," + default_cost);

        System.out.println("Number of Plans - heuristic ");


        for (int i = 0; i < 1; i++) {

            //System.out.println(" EXPERIMENT : " + (i + 1));

            AG = new HyperGraph();
            AG.generateRandomHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
            ArrayList<Artifact> requests = AG.getRequests();

            float number_of_plans = AG.PrintNumberOfPlans();


            /*
                           #### BEST FIRST ALGORITHM ####
            */
            startTime = System.nanoTime();
            AG.compute_the_latency_of_artifacts();
            endTime = System.nanoTime();
            long best_first = (endTime - startTime) / 1000000;


            System.out.println(number_of_plans + "," + best_first);
        }
    }
}
