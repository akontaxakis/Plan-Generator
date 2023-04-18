package Examples.Exact;

import Entities.Artifact;
import Entities.HyperEdge;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class ExhaustiveVSMinHeuristic {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;
        int pos = 0;
        Artifact[] artifacts = new Artifact[9];
        HyperEdge[] hyperEdges = new HyperEdge[10];
        artifacts[0] = new Artifact(Artifact.NodeType.ROOT, pos++);
        artifacts[1]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[2]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[3]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[4]  = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[5] = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[6] = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
        artifacts[7] = new Artifact(Artifact.NodeType.REQUEST, pos++);
        artifacts[8] = new Artifact(Artifact.NodeType.REQUEST, pos++);

        hyperEdges[0] = new HyperEdge(artifacts[0],artifacts[1],1);
        artifacts[1].addIN(hyperEdges[0]);
        hyperEdges[1]  = new HyperEdge(artifacts[0],artifacts[2],1);
        artifacts[2].addIN( hyperEdges[1]);
        hyperEdges[2] = new HyperEdge(artifacts[0],artifacts[3],1);
        artifacts[3].addIN(hyperEdges[2]);
        hyperEdges[3]  = new HyperEdge(artifacts[1],artifacts[4],2);
        artifacts[4].addIN(hyperEdges[3]);
        hyperEdges[4]= new HyperEdge(artifacts[2],artifacts[5],1);
        artifacts[5].addIN(hyperEdges[4]);
        hyperEdges[5] = new HyperEdge(artifacts[3],artifacts[6],2);
        artifacts[6].addIN(hyperEdges[5]);
        hyperEdges[6] = new HyperEdge(artifacts[4],artifacts[7],5);
        artifacts[7].addIN(hyperEdges[6]);
        hyperEdges[7] = new HyperEdge(artifacts[5],artifacts[7],6);
        artifacts[7].addIN(hyperEdges[7]);
        hyperEdges[8] = new HyperEdge(artifacts[5],artifacts[8],6);
        artifacts[8].addIN(hyperEdges[8]);
        hyperEdges[9] = new HyperEdge(artifacts[6],artifacts[8],5);
        artifacts[8].addIN(hyperEdges[9]);

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


            //fastPlan.print();
            /*
                         #### EXHAUSTIVE SEARCH ####
            */
            startTime = System.nanoTime();

            //Proposition_plan bestPlan = AG.exhaustiveSearch(tmpPLan,requests, new ArrayList<>());
            Proposition_plan bestPlan = AG.iterative_exhaustive(requests);
            System.out.println("Exact Number of Plans " + AG.numberOfPlans);
            System.out.println("Exact Number of Pops exhaustive" + AG.numberOfPops);
            //bestPlan.print();
            endTime = System.nanoTime();
            //System.out.println(" Time to run Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Exhaustive Cost is:" + bestPlan.getCost());

            bestPlan.print();
            System.out.println("Fast plan: "+fastPlan.getCost() +" Best Plan: "+bestPlan.getCost());
        }
    }


