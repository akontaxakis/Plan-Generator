package Examples.LimitedGraph;

import DirectedGraph.DirectedGraph;
import DirectedGraph.Edge;
import DirectedGraph.MaximumFlow;
import Entities.Artifact;
import Entities.HyperEdge;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;
import java.util.HashMap;

public class Collab_example_1 {


        public static void main(String[] args) {
            long startTime;
            long endTime;
            HyperGraph AG;
            int pos = 0;

            int l1=10;
            int l2=20;
            int l3=2;
            int l4=10000;
            int c1=10000;
            int c2=1;
            int c3=1;
            int c4=1;

            Artifact[] artifacts = new Artifact[5];
            HyperEdge[] hyperEdges = new HyperEdge[6];
            artifacts[0] = new Artifact(Artifact.NodeType.ROOT, pos++);
            artifacts[1]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            artifacts[2]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            artifacts[3]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            artifacts[4]  = new Artifact(Artifact.NodeType.REQUEST, pos++);

            //artifact 1
            hyperEdges[0] = new HyperEdge(artifacts[0],artifacts[1],l1);
            artifacts[1].setLoadCost(l1);
            artifacts[1].setComputeCost(c1);
            artifacts[1].addIN(hyperEdges[0]);

            //artifact 2
            hyperEdges[2]  = new HyperEdge(artifacts[1],artifacts[2],c2);
            artifacts[2].addIN(hyperEdges[2]);
            artifacts[2].setComputeCost(c2);
            hyperEdges[1]  = new HyperEdge(artifacts[0],artifacts[2],l2);
            artifacts[2].setLoadCost(l2);
            artifacts[2].addIN(hyperEdges[1]);


            //artifact 3
            hyperEdges[3] = new HyperEdge(artifacts[0],artifacts[3],l3);
            hyperEdges[4] = new HyperEdge(artifacts[1],artifacts[3],c3);
            artifacts[3].setLoadCost(l3);
            artifacts[3].setComputeCost(c3);
            artifacts[3].addIN(hyperEdges[3]);
            artifacts[3].addIN(hyperEdges[4]);

            //artifact 4
            hyperEdges[5] = new HyperEdge(artifacts[2],artifacts[4],c4);
            hyperEdges[5].addIN(artifacts[3]);
            artifacts[4].addIN(hyperEdges[5]);
            artifacts[4].setLoadCost(l4);
            artifacts[4].setComputeCost(c4);

            AG = new HyperGraph(artifacts,hyperEdges);

            ArrayList<Artifact> requests = AG.getRequests();
            System.out.println("requests: " + requests.size());
            System.out.println("Number of Plans");
            AG.PrintNumberOfPlans();


             /*
                                #### HEURISTIC ALGORITHM ####
            */
            startTime = System.nanoTime();

            Proposition_plan fastPlan = AG.Naive(requests);
            endTime = System.nanoTime();
            System.out.println(" Time to run Heuristic in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Heuristic Cost is:" + fastPlan.getCost());


        /*
                                #### Collaborative ALGORITHM ####
        */
            startTime = System.nanoTime();

            ArrayList<Artifact> loaded_artifacts = AG.collab_forward_pass();
            HyperGraph collab_plan = AG.collab_backward_pass(loaded_artifacts, requests);
            endTime = System.nanoTime();
            long collab_time =  ((endTime - startTime) / 1000000);
            System.out.println("collab Cost: " + collab_plan.EdgeCost());

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
            System.out.println("Helix Cost " + HelixPlan.EdgeCost());
            //fastPlan.print();
                /*
                           #### EXHAUSTIVE SEARCH ####
                */
            startTime = System.nanoTime();

            Proposition_plan bestPlan = AG.expand_closest_proposition(requests);
            //bestPlan.print();
            endTime = System.nanoTime();
            //System.out.println(" Time to run Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Exhaustive Cost is: " + bestPlan.getCost());
            System.out.println("Fast plan: "+fastPlan.getCost() +" Best Plan: "+bestPlan.getCost());



        }
    }

