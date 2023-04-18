package Examples.WinterSchool;

import DirectedGraph.DirectedGraph;
import DirectedGraph.MaximumFlow;
import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class Closest {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        int width = 200;
        int depth = 20000;
        int number_of_requests = 1;
        int materialiazed = 40;
        int artifact_in_degree = 0;
        int task_in_degree = 2;
        int task_out_degree = 1;
        int default_cost = 10;

        for (int i = 0; i < 100; i++) {
            AG = new HyperGraph();
            AG.generateRandomLimitedHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
            ArrayList<Artifact> requests = AG.getRequests();

                /*
                             #### Closest SEARCH ####
                */
            startTime = System.nanoTime();
            Proposition_plan closest_proposition = AG.expand_closest_proposition(requests);
            endTime = System.nanoTime();
            long closest_time = ((endTime - startTime) / 1000000);
            System.out.println(AG.getArtifactsSize() + "," + AG.getEdgeSize() +"," + closest_time );

        }
}
}