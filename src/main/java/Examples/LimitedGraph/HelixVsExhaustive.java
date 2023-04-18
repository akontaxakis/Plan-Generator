package Examples.LimitedGraph;

import DirectedGraph.DirectedGraph;
import DirectedGraph.Edge;
import Entities.Artifact;
import Entities.HyperEdge;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import DirectedGraph.MaximumFlow;
import java.util.ArrayList;
import java.util.HashMap;

public class HelixVsExhaustive {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;


        int width =100;
        int depth =100;
        int number_of_requests=3;
        int materialiazed = 60;
        int artifact_in_degree = 0;
        int task_in_degree=2;
        int task_out_degree =1;
        int default_cost = 10;


        AG = new HyperGraph();
        AG.generateRandomLimitedHG(number_of_requests, width, depth, materialiazed, artifact_in_degree, task_in_degree, task_out_degree, default_cost);
        ArrayList<Artifact> requests = AG.getRequests();
            System.out.println("requests: " + requests.size());
            System.out.println("Number of Plans");
            AG.PrintNumberOfPlans();
        for (Artifact aNumber : requests ) {
            System.out.println("Requests contains:" + aNumber.getPosition() );
        }
        Proposition_plan tmpPLan = new Proposition_plan();

            /*
                             #### HELIX SEARCH ####
            */
                /*
                             #### HELIX SEARCH ####
            */
        DirectedGraph g =  AG.toHelixGraph(requests.get(requests.size()-1).getPosition()*2+2);
        MaximumFlow mx = new MaximumFlow(g);
        ArrayList<Integer> flow = mx.getRisidualGraph(0, requests.get(requests.size()-1).getPosition()*2+2);
        System.out.println("RG2");

        HyperGraph HelixPlan = AG.flowToAG(flow);
        System.out.println(HelixPlan.EdgeCost());
        //fastPlan.print();
                /*
                             #### EXHAUSTIVE SEARCH ####
                */
        startTime = System.nanoTime();
        AG.compute_the_latency_of_artifacts();
        System.out.println("Latency Done");
        Proposition_plan bestPlan = AG.expand_closest_heuristic_search_Latency_MIN(requests);
        bestPlan.print();
        endTime = System.nanoTime();
        //System.out.println(" Time to run Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
                System.out.println(" Best Plan: "+bestPlan.getCost());

                  /*
                             #### EXHAUSTIVE SEARCH ####
                */
        startTime = System.nanoTime();
        Proposition_plan bestPlan_2 = AG.expand_closest_proposition(requests);
        //bestPlan_2.print();
        endTime = System.nanoTime();
        //System.out.println(" Time to run Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
        System.out.println(" Closest Plan: "+bestPlan_2.getCost() + " heuristic Search " + bestPlan.getCost() + " Helix Cost " + HelixPlan.EdgeCost());
        System.out.println(" Closest Plan: "+bestPlan_2.getCost() + " heuristic Search " + bestPlan.getCost() + " Helix Cost " + HelixPlan.EdgeCost());

}
}
