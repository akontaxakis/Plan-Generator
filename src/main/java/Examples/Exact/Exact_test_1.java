package Examples.Exact;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import HyperGraph.Proposition;
import java.util.ArrayList;

public class Exact_test_1 {
    public static void main(String[] args) {
        long startTime;
        long endTime;
        HyperGraph AG;

        int width =5;
        int depth =10;
        int number_of_requests=4;
        int materialiazed = 20;
        int artifact_in_degree = 2;
        int task_in_degree=3;
        int task_out_degree =2;
        int default_cost = 10;


        System.out.println("Test Parameters");
        System.out.println(number_of_requests+","+width +","+ depth +","+ materialiazed +","+  artifact_in_degree +","+ task_in_degree+","+  task_out_degree+","+ default_cost);

        System.out.println("Number of Plans(EX) - Number of Pops(EX) - TIME(EX)  -Number of Plans(DF) - Number of Pops(DF) - TIME(DF)   -Number of Plans(BF) - Number of Pops(BF) - TIME(BF)");

        String result = "";
        for(int i=0;i<100;i++){

            //System.out.println(" EXPERIMENT : " + (i + 1));

            AG = new HyperGraph();
            AG.generateRandomHG(number_of_requests,width, depth, materialiazed, artifact_in_degree,task_in_degree,task_out_degree,default_cost);
            ArrayList<Artifact> requests = AG.getRequests();

            float number_of_plans =  AG.PrintNumberOfPlans();

            /*
                        #### EXHAUSTIVE SEARCH ####
            */
            startTime = System.nanoTime();
            Proposition_plan best_exhastive_Plan = AG.iterative_exhaustive(requests);
            endTime = System.nanoTime();
            long exhastive_time = (endTime - startTime) / 1000000;

            //System.out.println("Exact Number of Plans " + AG.numberOfPlans);
            //System.out.println("Exact Number of Pops exhaustive " + AG.numberOfPops);

            result = result+""+AG.numberOfPlans+","+AG.numberOfPops+","+exhastive_time;
            AG.numberOfPlans =0;
            AG.numberOfPops =0;




            /*
                           #### BEST FIRST ALGORITHM ####
            */
            startTime = System.nanoTime();
            Proposition_plan best_plan_first = AG.best_plan_so_far(requests);
            endTime = System.nanoTime();
            long best_first = (endTime - startTime) / 1000000;

            //System.out.println("Number of Plans visited " + AG.numberOfPlans);
           // System.out.println("Number of Pops best_plan " + AG.numberOfPops);

            result = result+","+AG.numberOfPlans+","+AG.numberOfPops+","+best_first;

            AG.numberOfPlans =0;
            AG.numberOfPops =0;
           /*
                         #### CLOSEST FIRST SEARCH ####
            */
            startTime = System.nanoTime();
            Proposition_plan best_c_Plan = AG.expand_closest_proposition(requests);
            endTime = System.nanoTime();
            long closest_first = (endTime - startTime) / 1000000;

            //System.out.println("Number of Plans visited " + AG.numberOfPlans);
           // System.out.println("Exact Number of Pops closest/Best  " + AG.numberOfPops);

            result = result+","+AG.numberOfPlans+","+AG.numberOfPops+","+closest_first;

            AG.numberOfPlans =0;
            AG.numberOfPops =0;



            //System.out.println(number_of_plans+","+exhastive_time+","+best_first+","+closest_first);
            System.out.println(result);
            result="";
        }
    }
    }