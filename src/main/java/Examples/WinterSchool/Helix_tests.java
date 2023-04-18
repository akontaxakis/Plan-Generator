package Examples.WinterSchool;

import DirectedGraph.DirectedGraph;
import DirectedGraph.MaximumFlow;
import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class Helix_tests {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        int width = 2000;
        int depth = 10;
        int number_of_requests = 1;
        int materialiazed = 40;
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
                             #### HELIX SEARCH ####
        */
            startTime = System.nanoTime();
            DirectedGraph g = AG.toHelixGraph(requests.get(requests.size() - 1).getPosition() * 2 + 2);
            MaximumFlow mx = new MaximumFlow(g);
            ArrayList<Integer> flow = mx.getRisidualGraph(0, requests.get(requests.size() - 1).getPosition() * 2 + 2);

            HyperGraph HelixPlan = AG.flowToAG(flow);
            endTime = System.nanoTime();
            long helix_time = ((endTime - startTime) / 1000000);

            System.out.println(AG.getArtifactsSize() + "," + AG.getEdgeSize()  +","+ helix_time );
        }

}
}
