package Examples.Exact;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class Excact_without_exhaustive {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;

        int width = 20;
        int depth = 50;
        int number_of_requests = 10;
        int materialiazed = 20;
        int artifact_in_degree = 2;
        int task_in_degree = 2;
        int task_out_degree = 2;
        int default_cost = 10;


        System.out.println("Test Parameters");
        System.out.println(number_of_requests + "," + width + "," + depth + "," + materialiazed + "," + artifact_in_degree + "," + task_in_degree + "," + task_out_degree + "," + default_cost);

        System.out.println("Number of Plans - best_first - closest_first ");


        for (int i = 0; i < 100; i++) {

            //System.out.println(" EXPERIMENT : " + (i + 1));

            AG = new HyperGraph();
            AG.generateRandomHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
            ArrayList<Artifact> requests = AG.getRequests();

            float number_of_plans = AG.PrintNumberOfPlans();


            /*
                           #### BEST FIRST ALGORITHM ####
            */
            startTime = System.nanoTime();
            Proposition_plan best_plan_first = AG.best_plan_so_far(requests);
            endTime = System.nanoTime();
            long best_first = (endTime - startTime) / 1000000;

           /*
                         #### CLOSEST FIRST SEARCH ####
            */
            startTime = System.nanoTime();
            Proposition_plan best_c_Plan = AG.expand_closest_proposition(requests);
            endTime = System.nanoTime();
            long closest_first = (endTime - startTime) / 1000000;


            System.out.println(number_of_plans + "," + best_first + "," + closest_first);
        }
    }
}