package Experiments.Retrieval;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.*;
import java.util.stream.Collectors;

public class Random_Artifact_retrieval_experiment {

    public static void main(String[] args) {
        String project_path = "C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\";
        lib utils = new lib();

        String uid = "all_in_one";

        uid = "TAXI_graph_100_ad";

        uid = "HIGGS_10_200_f";
        uid = "HIGGS_budget_study";
        uid = "TAXI_10_200_f6";
        uid = "TAXI_budget_study";
        uid = "TAXI_ADVANCED";
        uid = "materialization_2";
        //uid = "TAXI_budget_study";
        double edge_cost_modifier = 1000;
        int iterations =49;
        int budget = 0;
        int N = 5;
        int number_of_experiments = 10;
        int number_of_total_requests = 10;

        for (int number_of_requests = 1; number_of_requests < number_of_total_requests + 1; number_of_requests++) {
            //AG.print();
            double no_shared_cummulative_cost = 0;
            double collab_cummulative_cost = 0;
            double shared_cummulative_cost = 0;
            double limited_cummulative_cost = 0;
            double equivalent_cummulative_cost = 0;
            double equivalent_no_sharing_cummulative_cost = 0;

        for (int k = 0; k < N; k++) {

            HyperGraph shared = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + k, iterations, "shared");
            HyperGraph helix = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + k, iterations, "helix");
            HyperGraph limited = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + k, iterations, "limited");
            HyperGraph mat = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + k, iterations, "limited");
            HyperGraph equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + k, iterations, "equivalent");
            HyperGraph no_sharing_equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + k, iterations, "equivalent");
            HyperGraph no_sharing = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + k, iterations, "shared");


            ////////////////////////////REMOVE SUPER NODES/////////////////////////////////
            HyperGraph true_no_sharing = utils.TurnToTrueHG(no_sharing);

            HyperGraph true_shared = utils.TurnToTrueHG(shared);
            HyperGraph true_limited = utils.TurnToTrueHG(limited);
            HyperGraph true_no_sharing_equivalent = utils.TurnToTrueHG(no_sharing_equivalent);
            HyperGraph true_equivalent = utils.TurnToTrueHG(equivalent);

            //models.remove("De_0.7");
            //models.remove("Ra_0.8");
            true_limited.compute_the_latency_of_artifacts();
            true_shared.compute_the_latency_of_artifacts();
            true_equivalent.compute_the_latency_of_artifacts();

                ArrayList<String> artifacts = true_shared.getArtifactsIDs("no_scores");
                int no_plan = 0;
                for (int i = 0; i < number_of_experiments; i++) {

                    Set<String> request = utils.getRandomElements(artifacts.stream().collect(Collectors.toSet()), number_of_requests);

                    ArrayList<String> eqSet = utils.euqivalentRequests(new ArrayList<>(request));

                    //ArrayList<Artifact> a_requests = AG.getRequests(request);

                    //Proposition_plan best_c_Plan = AG.expand_closest_proposition(a_requests);
                    double no_sharing_cost = 0;

                    for(String r: request){

                        HashSet<String> req = new HashSet<>();
                        req.add(r);
                        ArrayList<Artifact> no_sharing_request = true_no_sharing.getRequests(req);
                        Proposition_plan no_sh_plan = true_shared.expand_closest_proposition(no_sharing_request);
                        no_sharing_cost = no_sharing_cost + no_sh_plan.getCost();

                    }
                    double no_sharing_euqivalent_cost = 0;
                    for(String eq_r: eqSet){

                        HashSet<String> req = new HashSet<>();
                        req.add(eq_r);
                        ArrayList<Artifact> eq_no_sharing_request = true_no_sharing_equivalent.getRequests(req);
                        Proposition_plan no_sh_eq_plan = true_no_sharing_equivalent.expand_closest_proposition(eq_no_sharing_request);
                        no_sharing_euqivalent_cost = no_sharing_euqivalent_cost + no_sh_eq_plan.getCost();

                    }




                    ArrayList<Artifact> sh_rq = true_shared.getRequests(new HashSet<>(request));
                    ArrayList<Artifact> lt_rq = true_limited.getRequests(new HashSet<>(request));
                    ArrayList<Artifact> eq_rq = true_equivalent.getRequests(new HashSet<>(eqSet));

                    ArrayList<Artifact> loaded_artifacts = true_limited.collab_forward_pass();
                    HyperGraph collab_plan = true_limited.collab_backward_pass(loaded_artifacts, lt_rq);




                    Proposition_plan sh_plan = true_shared.expand_closest_proposition(sh_rq);
                    Proposition_plan lt_plan = true_limited.expand_closest_proposition(lt_rq);
                    Proposition_plan eq_plan = true_equivalent.expand_closest_proposition(eq_rq);

                    //double collab_cost = collab_plan.EdgeCost();
                    double sh_cost = sh_plan.getCost();
                    double lt_cost = lt_plan.getCost();
                    double eq_cost = eq_plan.getCost();

                    no_shared_cummulative_cost = no_shared_cummulative_cost + no_sharing_cost;
                    shared_cummulative_cost = shared_cummulative_cost + sh_cost;
                    limited_cummulative_cost = limited_cummulative_cost + lt_cost;
                    equivalent_no_sharing_cummulative_cost = equivalent_no_sharing_cummulative_cost + no_sharing_euqivalent_cost;
                    equivalent_cummulative_cost = equivalent_cummulative_cost + eq_cost;
                    //collab_cummulative_cost = collab_cummulative_cost + collab_cost;

                }
                // System.out.println(number_of_requests + "," + shared_cummulative_cost / (number_of_experiments*edge_cost_modifier) + "," + limited_cummulative_cost /(number_of_experiments*edge_cost_modifier) + "," + equivalent_cummulative_cost /(number_of_experiments*edge_cost_modifier));
            }
            System.out.println(number_of_requests + "," + collab_cummulative_cost/(number_of_experiments*edge_cost_modifier*N) + "," + no_shared_cummulative_cost/ (number_of_experiments*edge_cost_modifier*N)+ "," + shared_cummulative_cost / (number_of_experiments*edge_cost_modifier*N) + "," + limited_cummulative_cost / (number_of_experiments*edge_cost_modifier*N) + "," + equivalent_no_sharing_cummulative_cost/ (number_of_experiments*edge_cost_modifier*N) + "," + equivalent_cummulative_cost / (number_of_experiments*edge_cost_modifier*N));
        }
    }
}