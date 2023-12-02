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

public class Helix_example_1 {


        public static void main(String[] args) {
            long startTime;
            long endTime;
            HyperGraph AG;
            int pos = 0;

            int l1=1;
            int l2=3 ;
            int l3=10000;
            int c1=10000;
            int c2=1;
            int c3=4;


            Artifact[] artifacts = new Artifact[4];
            HyperEdge[] hyperEdges = new HyperEdge[4];
            artifacts[0] = new Artifact(Artifact.NodeType.ROOT, pos++);
            artifacts[1]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            artifacts[2]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            artifacts[3]  = new Artifact(Artifact.NodeType.REQUEST, pos++);
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
            hyperEdges[3] = new HyperEdge(artifacts[2],artifacts[3],c3);
            artifacts[3].setLoadCost(l3);
            artifacts[3].setComputeCost(c3);
            artifacts[3].addIN(hyperEdges[3]);

            AG = new HyperGraph(artifacts,hyperEdges);

            ArrayList<Artifact> requests = AG.getRequests();
            System.out.println("requests: " + requests.size());
            System.out.println("Number of Plans");
            AG.PrintNumberOfPlans();

            Proposition_plan tmpPLan = new Proposition_plan();

             /*
                                #### HEURISTIC ALGORITHM ####
            */
            startTime = System.nanoTime();

            Proposition_plan fastPlan = AG.Naive(tmpPLan,requests, new ArrayList<>());
            endTime = System.nanoTime();
            System.out.println(" Time to run Heuristic in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Heuristic Cost is:" + fastPlan.getCost());

            /*
                            #### HELIX SEARCH ####
            */
            DirectedGraph g =  AG.toHelixGraph(requests.get(requests.size()-1).getPosition()*2+2);
            System.out.println(g.print());
            MaximumFlow mx = new MaximumFlow(g);
            HashMap<Edge, Integer> flow = mx.getMaxFlow(0, requests.get(requests.size()-1).getPosition()*2+2);
            mx.printFlow(flow,0);
            System.out.println(mx.getFlowSize(flow,0));
            //fastPlan.print();
                /*
                           #### EXHAUSTIVE SEARCH ####
                */
            startTime = System.nanoTime();

            //Proposition_plan bestPlan = AG.exhaustiveSearch(tmpPLan,requests, new ArrayList<>());
            //bestPlan.print();
            endTime = System.nanoTime();
            //System.out.println(" Time to run Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
            //System.out.println("The MIN Exhaustive Cost is:" + bestPlan.getCost());

            //bestPlan.print();
            //System.out.println("Fast plan: "+fastPlan.getCost() +" Best Plan: "+bestPlan.getCost());



        }
    }

