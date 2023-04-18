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

public class Helix_example_2 {


    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        Artifact[] artifacts = new Artifact[6];
        HyperEdge[] hyperEdges = new HyperEdge[10];
        artifacts[0] = new Artifact(Artifact.NodeType.ROOT, pos++);
        artifacts[1]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[2]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[3]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[4]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[5] = new Artifact(Artifact.NodeType.REQUEST, pos++);

        int l1=1;
        int l2=3;
        int c1=1000;
        int c2=1000;

        int l3=4;
        int c3=1;
        int l4 =2;
        int c4 = 1;

        int l5 =4;
        int c5 = 1;


        //Artifact 1
        hyperEdges[0] = new HyperEdge(artifacts[0],artifacts[1],l1);
        artifacts[1].setLoadCost(l1);
        artifacts[1].setComputeCost(c1);
        artifacts[1].addIN(hyperEdges[0]);

        //Artifact 2
        hyperEdges[1]  = new HyperEdge(artifacts[0],artifacts[2],l2);
        artifacts[2].setLoadCost(l2);
        artifacts[2].setComputeCost(c2);
        artifacts[2].addIN(hyperEdges[1]);

        //Artifact 3
        hyperEdges[2]  = new HyperEdge(artifacts[0],artifacts[3],l3);
        artifacts[3].addIN(hyperEdges[2]);
        hyperEdges[3] = new HyperEdge(artifacts[1],artifacts[3],c3);
        artifacts[3].addIN(hyperEdges[3]);
        artifacts[3].setLoadCost(l3);
        artifacts[3].setComputeCost(c3);


        //Artifact 4
        hyperEdges[4]  = new HyperEdge(artifacts[0],artifacts[4],l4);
        artifacts[4].setLoadCost(l4);

        artifacts[4].addIN(hyperEdges[4]);

        hyperEdges[5] = new HyperEdge(artifacts[2],artifacts[4],c4);
        artifacts[4].addIN(hyperEdges[5]);
        artifacts[4].setComputeCost(c4);
        //Artifact 5
        hyperEdges[5]= new HyperEdge(artifacts[3],artifacts[5],c5);
        hyperEdges[5].addIN(artifacts[4]);
        artifacts[5].addIN(hyperEdges[5]);
        artifacts[5].setComputeCost(c5);
        artifacts[5].setLoadCost(10000);


        AG = new HyperGraph(artifacts,hyperEdges);

        ArrayList<Artifact> requests = AG.getRequests();
        System.out.println("requests: " + requests.size() +"request " + requests.get(0).getPosition());
        System.out.println("Number of Plans");
        AG.PrintNumberOfPlans();

        Proposition_plan tmpPLan = new Proposition_plan();

             /*
                                #### HEURISTIC ALGORITHM ####
            */
        startTime = System.nanoTime();
        AG.compute_the_latency_of_artifacts();
        Proposition_plan Heuristic_search_Plan = AG.expand_closest_heuristic_search_Latency_MIN(requests);
        endTime = System.nanoTime();
        long h_time =  ((endTime - startTime) / 1000000);
        System.out.println("The MIN Heuristic Cost is:" + Heuristic_search_Plan.getCost());


             /*
                                #### Collaborative ALGORITHM ####
            */
        startTime = System.nanoTime();

        ArrayList<Artifact> loaded_artifacts = AG.collab_forward_pass();
        HyperGraph collab_plan = AG.collab_backward_pass(loaded_artifacts, requests);
        endTime = System.nanoTime();
        long collab_time =  ((endTime - startTime) / 1000000);
        System.out.println("The Collab cost is:" + collab_plan.EdgeCost());




            /*
                             #### HELIX SEARCH ####
            */
        startTime = System.nanoTime();
        DirectedGraph g =  AG.toHelixGraph(requests.get(requests.size()-1).getPosition()*2+2);
        MaximumFlow mx = new MaximumFlow(g);
        ArrayList<Integer> flow = mx.getRisidualGraph(0, requests.get(requests.size()-1).getPosition()*2+2);

        HyperGraph HelixPlan = AG.flowToAG(flow);
        endTime = System.nanoTime();
        long helix_time =  ((endTime - startTime) / 1000000);
        System.out.println(HelixPlan.EdgeCost());
        //fastPlan.print();




                /*
                             #### EXHAUSTIVE SEARCH ####
                */
        startTime = System.nanoTime();

        Proposition_plan bestPlan = AG.iterative_exhaustive(requests);
        //bestPlan.print();
        endTime = System.nanoTime();
        //System.out.println(" Time to run Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
        System.out.println("The MIN Exhaustive Cost is:" + bestPlan.getCost());
        long exhaustive_time =  ((endTime - startTime) / 1000000);
        bestPlan.print();
        System.out.println("Fast plan: "+Heuristic_search_Plan.getCost() +" Best Plan: "+bestPlan.getCost());



    }

}

