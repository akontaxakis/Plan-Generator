package Examples.WinterSchool;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class Collab {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        int width = 20000;
        int depth = 100;
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
                                #### Collaborative ALGORITHM ####
        */
            startTime = System.nanoTime();

            ArrayList<Artifact> loaded_artifacts = AG.collab_forward_pass();
            HyperGraph collab_plan = AG.collab_backward_pass(loaded_artifacts, requests);
            endTime = System.nanoTime();
            long collab_time = ((endTime - startTime) / 1000000);
            System.out.println(AG.getArtifactsSize() + "," + AG.getEdgeSize() +"," + collab_time );

        }
}
}
