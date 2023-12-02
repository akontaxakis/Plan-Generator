package Experiments.Scalability;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class Equivalent {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        lib utils = new lib();
        HyperGraph AG;
        int pos = 0;
        int width = 100;
        int depth = 30;
        int number_of_requests = 5;
        int materialiazed = 30;
        int artifact_in_degree = 2;
        int task_in_degree = 2;
        int task_out_degree = 2;
        int default_cost = 20;
        int n =10;
        System.out.println(number_of_requests + "," + width + "," + depth + "," + materialiazed + "," + artifact_in_degree + "," + task_in_degree + "," + task_out_degree + "," + default_cost);
        AG = new HyperGraph();
        AG.generateRandomHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
        AG.compute_the_latency_of_artifacts();
        long stack_time=0;
        long queue_time=0;
        long priority_queue_time=0;
        long heuristic_queue_time=0;
        for (int i = 0; i < n; i++) {
            ArrayList<String> artifacts = AG.getArtifactsIDs("empty");
            Set<String> request = utils.getRandomElements(artifacts.stream().collect(Collectors.toSet()), number_of_requests);
            ArrayList<Artifact> requests = AG.getRequests(request);

                  /*
                             #### priortiy_queue_search ####
                */
            startTime = System.nanoTime();
            Proposition_plan priority_que_proposition = AG.priortiy_queue_search(requests);
            endTime = System.nanoTime();
            priority_queue_time = priority_queue_time+ ((endTime - startTime) / 1000000);

        /*
                                #### stack_search ALGORITHM ####
        */
            startTime = System.nanoTime();
            //AG.compute_the_latency_of_artifacts();
            Proposition_plan exhaustive_search_Plan = AG.stack_search(requests);
            endTime = System.nanoTime();
            stack_time = stack_time +((endTime - startTime) / 1000000);
            long number_of_plans = AG.numberOfPlans;

                /*
                             #### queue_search ####
                */
            startTime = System.nanoTime();
            Proposition_plan closest_proposition = AG.queue_search(requests);
            endTime = System.nanoTime();
            queue_time =queue_time+ ((endTime - startTime) / 1000000);



            System.out.println(i+","+stack_time + "," + queue_time+ "," + priority_queue_time + ","  +heuristic_queue_time);

        }
        System.out.println(stack_time/n + "," + queue_time/n + "," + priority_queue_time/n + "," + heuristic_queue_time/n);
    }
}
