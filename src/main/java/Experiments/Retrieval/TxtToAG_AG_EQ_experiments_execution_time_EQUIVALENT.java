package Experiments.Retrieval;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.*;

public class TxtToAG_AG_EQ_experiments_execution_time_EQUIVALENT {

    public static void main(String[] args) {
        lib utils = new lib();
        //String uid = "AG_3";
        //String uid = "playground";
        //String uid = "playground_commercial_4";
        String uid = "all_in_one";
        uid = "test_sampling";
        uid = "sampling_50";
        uid = "sampling_200_sharing";
        uid = "all_steps";
        uid = "testing_sampling";
        //String uid = "playground_commercial_2_people";
        HashMap<String, ArrayList<String>> models = utils.get_models_from_file("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\metrics\\" + uid + "_scores.txt" );
        HyperGraph AG = utils.TxtToHyperGraph("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\graphs\\" + uid + "_EDGES_AG_100_eq_classifier_breast_cancer.txt","time" );
        HyperGraph AG_2 = utils.TxtToHyperGraph("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\graphs\\" + uid + "_EDGES_AG_100_lt_classifier_breast_cancer.txt" ,"time");
        AG.compute_the_latency_of_artifacts();
        //models.remove("De_0.7");
        //models.remove("Ra_0.8");
        AG_2.compute_the_latency_of_artifacts();
        int number_of_experiments = 100;
        int number_of_total_requests = 10;
        AG.setAllRequests();
        System.out.println("number of models trained:" +models.size());
        System.out.println("number of models in the equivalent graph:" +AG.getNumberOfRequests());
        AG_2.setAllRequests();
        long startTime,endTime;
       for(int number_of_requests=1;number_of_requests<number_of_total_requests+1;number_of_requests++) {
           //AG.print();
           double total_benefit = 0;
           double maximum_gain =0;
           double maximum_gain_precentage = 0;
           double minimum_gain_precentage = 0;
           double minimum_gain = 1000000000;
           double eq_algorithm_ext = 0;
           double nv_algorithm_ext = 0;
           double lt_algorithm_ext = 0;
           long eq_execution_time = 0;
           long lt_execution_time =0;
           long nv_execution_time =0;
           long ex_execution_time =0;
           double eq_total_latency =0;
           double lt_total_latency =0;

           int no_plan = 0;
           int alternative_models =0;
           for (int i = 0; i < number_of_experiments; i++) {

               Set<String> request = utils.getRandomElements(models.keySet(), number_of_requests);


               ArrayList<Artifact> a_requests = AG.getRequests(request);
               startTime = System.nanoTime();
               Proposition_plan best_c_Plan = AG.expand_closest_proposition(a_requests);
               endTime = System.nanoTime();
               eq_algorithm_ext = eq_algorithm_ext+ (endTime - startTime) / 1000000;

               //NAIVE APPROACH
               startTime = System.nanoTime();
               int ex_time = 0;
                for(Artifact a: a_requests){
                    ArrayList<Artifact> list = new ArrayList<Artifact>();
                    list.add(a);
                    Proposition_plan ex_c_Plan = AG.expand_closest_proposition(list);
                    ex_time = ex_time +ex_c_Plan.getCost();
                }
               endTime = System.nanoTime();
               nv_algorithm_ext =  nv_algorithm_ext+ (endTime - startTime) / 1000000;


               //get a proposition for the limited_graph
               List<List<String>> propositions = utils.getAllCombinations(models, request);
               Collections.shuffle(propositions);
               List<String> request_names = propositions.get(0);
               ArrayList<Artifact> request_test = AG_2.getRequests(new HashSet<>(request_names));

               //Limited Graph no sharing
               int nv_time = 0;
               for(Artifact a: request_test){
                   ArrayList<Artifact> list = new ArrayList<Artifact>();
                   list.add(a);
                   Proposition_plan nv_c_Plan = AG_2.expand_closest_proposition(list);
                   nv_time = nv_time +nv_c_Plan.getCost();
               }

               //LIMITED GRAPH ALGORITHM
               int minimum_cost = 100000000;
               Proposition_plan tmp = new Proposition_plan();
               tmp.setCost(-1);
               startTime = System.nanoTime();


               Proposition_plan best_c_Plan_2 = AG_2.expand_closest_proposition(request_test);
               tmp = best_c_Plan_2;

               endTime = System.nanoTime();
               lt_algorithm_ext = lt_algorithm_ext+ (endTime - startTime) / 1000000;
               if ((best_c_Plan.getCost() > tmp.getCost() && tmp.getCost()>0 ) || tmp.getCost() == -1) {
                   no_plan++;
                  // best_c_Plan.print();
                  // tmp.print();
               } else {
                   double benefit = tmp.getCost() - best_c_Plan.getCost();
                   total_benefit = total_benefit + benefit;
                   eq_execution_time = eq_execution_time +  best_c_Plan.getCost();
                   lt_execution_time =lt_execution_time + tmp.getCost();
                   nv_execution_time = nv_execution_time + nv_time;
                   ex_execution_time = ex_execution_time + ex_time;
                   eq_total_latency =eq_total_latency +  AG.getLatency(a_requests);;
                   lt_total_latency =lt_total_latency+ AG_2.getLatency(request_test);
                        if(benefit >maximum_gain){
                            maximum_gain = benefit;
                            maximum_gain_precentage= benefit/tmp.getCost();
                            if(maximum_gain_precentage == 1){
                                System.out.println("WTF");
                            }
                        }
                        if(benefit < minimum_gain){
                            minimum_gain = benefit;
                            minimum_gain_precentage = benefit/tmp.getCost();
                        }


                   }
           }
            if(no_plan<number_of_experiments) {
                //                  Number of Requests	                average time saved per request	                       EQ_total_execution_time| LT_total_execution_time | NV_total_execution_time | eq_algorithm_time	|lt_algorithm_execution | NV_algorithm_execution	| average_alternative_plans
                System.out.println(number_of_requests + "," + (double) total_benefit / (number_of_experiments - no_plan) + "," + eq_execution_time + "," + lt_execution_time + "," + nv_execution_time+ ","+ ex_execution_time + "," + eq_algorithm_ext + "," + lt_algorithm_ext + "," + nv_algorithm_ext  + "," + alternative_models / (number_of_experiments - no_plan) + "," +maximum_gain + "," +minimum_gain+ "," + maximum_gain_precentage + "," + minimum_gain_precentage+ "," +eq_total_latency + "," +lt_total_latency+ "," +no_plan);
                //System.out.println("Lantecy");
                //System.out.println(eq_total_latency / (number_of_experiments - no_plan) + "," + lt_total_latency / (number_of_experiments - no_plan));
            }
       }
        System.out.println("EQ graph description" );
        AG.stats();
        System.out.println("LT graph description" );
        AG_2.stats();
    }

}