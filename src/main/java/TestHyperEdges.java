import Entities.Artifact;
import Entities.Plan;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import HyperGraph.Proposition;
import java.util.ArrayList;

public class TestHyperEdges {

    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;

        for(int i=0;i<100;i++){

            System.out.println(" EXPERIMENT : " + (i + 1));

            AG = new HyperGraph();
            AG.generateRandomHG(5,10, 10, 20, 2,2,2,10);
            ArrayList<Artifact> requests = AG.getRequests();

            float number_of_plans =  AG.PrintNumberOfPlans();
            System.out.println("Number of Plans: "+number_of_plans);
            /*
                            #### BEST ALGORITHM ####
            */
            startTime = System.nanoTime();

           // Proposition_plan fastPropostionPlan = AG.Naive(tmpPLan,requests, new ArrayList<>());
            Proposition_plan best_plan_first = AG.best_plan_so_far(requests);
            endTime = System.nanoTime();
            System.out.println(" Time to run best first in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The best first Cost is:" + best_plan_first.getCost());

            //fastPlan.print();
            /*
                         #### EXHAUSTIVE SEARCH ####
            */
            startTime = System.nanoTime();
            //Proposition_plan bestPlan = AG.exhaustiveSearch(tmpPLan,requests, new ArrayList<>());
            Proposition_plan bestPlan = AG.iterative_exhaustive(requests);
            endTime = System.nanoTime();
            System.out.println(" Time to run Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Exhaustive Cost is:" + bestPlan.getCost());

           /*
                         #### HEURISTIC SEARCH ####
            */
            startTime = System.nanoTime();
            Proposition_plan best_h_Plan = AG.expand_closest_heuristic_search_MAX(requests);
            //Proposition_plan best_c_Plan = AG.ClosestProposition(tmpPLan,requests, new ArrayList<>());
            endTime = System.nanoTime();
            System.out.println(" Time to run heuristic_search in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The heuristic_search Cost is:" + best_h_Plan.getCost());


            /*

            /*

                       /*
                         #### Closest SEARCH ####
            */
            startTime = System.nanoTime();
            Proposition_plan best_c_Plan = AG.expand_closest_proposition(requests);
            //Proposition_plan best_c_Plan = AG.ClosestProposition(tmpPLan,requests, new ArrayList<>());
            endTime = System.nanoTime();
            System.out.println(" Time to run ClosestProposition in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The ClosestProposition Cost is:" + best_c_Plan.getCost());

            //bestPlan.print();
            System.out.println("Fast plan: "+best_plan_first.getCost() +" Best Plan: "+bestPlan.getCost() +" Closest Plan: "+best_c_Plan.getCost() +" Heuristic Plan: "+best_h_Plan.getCost());


            /*


                         #### EXHAUSTIVE SEARCH ####

            startTime = System.nanoTime();
            tmpPLan = new Propostion_Plan(0);
            tmpPLan.add(0,new Proposition(0,requests));
            ArrayList<Plan> plans = AG.Simple_Exhaustive_Search(0);
            Plan best_simple_Plan = minimumCostPlan(plans);
            endTime = System.nanoTime();
            System.out.println(" Time to run Simple_Exhaustive in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Simple_Exhaustive Cost is:" + best_simple_Plan.getCost());

           // bestPlan.print();
            System.out.println("Fast plan: "+fastPropostionPlan.getCost() +" Best Plan: "+bestPlan.getCost());
    */

        }
    }
    private static Plan minimumCostPlan(ArrayList<Plan> validPlans) {
        int min = 1000000;
        Plan bestNPlan = new Plan();
        for(Plan p: validPlans){
            if(p.getCost()<min){
                min = p.getCost();
                bestNPlan = p;
            }
        }
        return bestNPlan;
    }
}



//System.out.println(" Time to Generate graph in milliseconds " + (endTime - startTime) / 1000000);

//AG.print();
//System.out.println(requests.size());
// ArrayList<Proposition> propositions =  new ArrayList<>();
//    AG.generatePropositions(0,0,propositions, requests);
           /*
            for(Proposition p: propositions){
                p.print();
            }
            */